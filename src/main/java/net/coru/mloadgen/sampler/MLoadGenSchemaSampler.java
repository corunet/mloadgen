/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package net.coru.mloadgen.sampler;

import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.COLLECTION;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.DBNAME;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.FILTER;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.INSERT_OPERATION;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.MONGODB_HOSTNAME;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.MONGODB_PASSWORD;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.MONGODB_PORT;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.MONGODB_URL;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.MONGODB_USERNAME;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.OPERATION;
import static net.coru.mloadgen.sampler.MLoadGenConfigHelper.UPDATE_OPERATION;
import static net.coru.mloadgen.util.PropsKeysHelper.SCHEMA_PROPERTIES;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.MongoSecurityException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.processor.JsonSchemaProcessor;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.bson.Document;
import org.slf4j.Logger;

public class MLoadGenSchemaSampler extends AbstractJavaSamplerClient {

  private Logger log;

  private MongoClient mongoClient;

  private MongoDatabase database;

  private final JsonSchemaProcessor processor = new JsonSchemaProcessor();

  @Override
  public Arguments getDefaultParameters() {
    Arguments defaultParameters = new Arguments();
    defaultParameters.addArgument(MONGODB_HOSTNAME, "localhost");
    defaultParameters.addArgument(MONGODB_PORT, "27017");
    defaultParameters.addArgument(MONGODB_USERNAME, "<username>");
    defaultParameters.addArgument(MONGODB_PASSWORD, "<password>");
    defaultParameters.addArgument(MONGODB_URL, "<mongo url>");
    defaultParameters.addArgument(DBNAME, "<dbname>");
    defaultParameters.addArgument(OPERATION, "insert");
    return defaultParameters;
  }

  @Override
  public void setupTest(JavaSamplerContext context) {
    log = getNewLogger();
    if (null == database) {

      final String hostname = context.getParameter(MONGODB_HOSTNAME);
      final int port = Integer.parseInt(context.getParameter(MONGODB_PORT));
      String dbName = context.getParameter(DBNAME);
      String username = context.getParameter(MONGODB_USERNAME);
      String url = context.getParameter(MONGODB_URL);
      char[] password = context.getParameter(MONGODB_PASSWORD).toCharArray();

      MongoCredential credential = MongoCredential.createCredential(username, dbName, password);
      MongoClientSettings settings = MongoClientSettings.builder()
          .credential(credential)
          .applyToClusterSettings(builder ->
              builder.hosts(Collections.singletonList( new ServerAddress(hostname, port))).build())
          .build();

      mongoClient = mongoUrlValidation(url)? MongoClients.create(url) : MongoClients.create(settings);

      String collection = context.getJMeterVariables().get(COLLECTION);
      database = mongoClient.getDatabase(dbName);

      try {
        List<String> colNameList = new ArrayList<>();
        database.listCollectionNames().into(colNameList);

        if (!colNameList.contains(collection)) {
          log.error("Collection {} doesn't exist in database", collection);
          throw new IllegalArgumentException("Collection " + collection + " doesn't exist in Database" );
        }
      } catch (MongoCommandException | MongoSecurityException ex) {
        log.warn("No permission to check if collection exists. Continuing operation");
      }

      List<FieldValueMapping> schemaProperties = (List<FieldValueMapping>) context.getJMeterVariables().getObject(SCHEMA_PROPERTIES);
      processor.processSchema(schemaProperties);
    }
    super.setupTest(context);
  }

  @Override
  public void teardownTest(JavaSamplerContext context) {
    super.teardownTest(context);
    mongoClient.close();
  }

  public SampleResult runTest(JavaSamplerContext context) {
    SampleResult sampleResult = new SampleResult();
    sampleResult.sampleStart();
    String collection = context.getJMeterVariables().get(COLLECTION);

    try {

      String document = processor.next().toString();
      String result;
      if (INSERT_OPERATION.equalsIgnoreCase(context.getParameter(OPERATION))) {
        sampleResult.setSamplerData(document);
        result = runInsertCommand(collection, document);
      } else if (UPDATE_OPERATION.equalsIgnoreCase(context.getParameter(OPERATION))) {
        sampleResult.setSamplerData(document);
        String filter = context.getParameter(FILTER);
        result = runUpdateCommand(collection, filter, document);
      } else {
        throw new IllegalArgumentException("Wrong operation " + context.getParameter(OPERATION));
      }
      sampleResult.setResponseData(result, StandardCharsets.UTF_8.name());
      sampleResult.setSuccessful(true);
      log.info("Following operation had been executed in the collection {} : {}", collection, context.getParameter(OPERATION));
    } catch (IllegalArgumentException ex) {
      log.error("Wrong collection name {}", collection, ex);
      setErrorSampleResult(sampleResult, ex);
    } catch (MongoWriteException ex) {
      log.error("Error trying to insert document", ex);
      setErrorSampleResult(sampleResult, ex);
    } catch (MongoWriteConcernException ex) {
      log.error("Error trying to fulfil write document", ex);
      setErrorSampleResult(sampleResult, ex);
    } catch (MongoException ex) {
      log.error("Unexpected Mongo error when trying to write document", ex);
      setErrorSampleResult(sampleResult, ex);
    }
    sampleResult.sampleEnd();
    return sampleResult;
  }

  private void setErrorSampleResult(SampleResult sampleResult, RuntimeException ex) {
    sampleResult.setErrorCount(sampleResult.getErrorCount() + 1);
    sampleResult.setResponseData(ex.getMessage(),  StandardCharsets.UTF_8.name());
    sampleResult.setSuccessful(false);
  }

  private String runInsertCommand(String collection, String document) {
    Document mongoDocument = Document.parse(document);

    database.getCollection(collection).insertOne(mongoDocument);
    return document;
  }

  private String runUpdateCommand(String collection, String filter,  String document) {
    BasicDBObject mongoFilter = BasicDBObject.parse(filter);
    Document mongoDocument = Document.parse(document);

    UpdateResult response = database.getCollection(collection).updateOne(mongoFilter, mongoDocument);

    return response.toString();
  }

  private Boolean mongoUrlValidation(String mongoUrl) {
    if(mongoUrl.isEmpty() || Objects.isNull(mongoUrl) || mongoUrl.equals("<mongo url>"))
    {
      return false;
    }
    else {
      return true;
    }
  }
}
