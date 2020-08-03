/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package net.coru.mloadgen;

import static net.coru.mloadgen.MLoadGenConfigHelper.COLLECTION;
import static net.coru.mloadgen.MLoadGenConfigHelper.DBNAME;
import static net.coru.mloadgen.MLoadGenConfigHelper.DOCUMENT;
import static net.coru.mloadgen.MLoadGenConfigHelper.FILTER;
import static net.coru.mloadgen.MLoadGenConfigHelper.INSERT_OPERATION;
import static net.coru.mloadgen.MLoadGenConfigHelper.MONGODB_HOSTNAME;
import static net.coru.mloadgen.MLoadGenConfigHelper.MONGODB_PASSWORD;
import static net.coru.mloadgen.MLoadGenConfigHelper.MONGODB_PORT;
import static net.coru.mloadgen.MLoadGenConfigHelper.MONGODB_USERNAME;
import static net.coru.mloadgen.MLoadGenConfigHelper.OPERATION;
import static net.coru.mloadgen.MLoadGenConfigHelper.QUERY_OPERATION;
import static net.coru.mloadgen.MLoadGenConfigHelper.UPDATE_OPERATION;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.bson.Document;
import org.slf4j.Logger;

public class MLoadGen extends AbstractJavaSamplerClient {

  private Logger log;

  private MongoClient mongoClient;

  private MongoDatabase database;

  @Override
  public Arguments getDefaultParameters() {
    Arguments defaultParameters = new Arguments();
    defaultParameters.addArgument(MONGODB_HOSTNAME, "localhost");
    defaultParameters.addArgument(MONGODB_PORT, "27017");
    defaultParameters.addArgument(MONGODB_USERNAME, "<username>");
    defaultParameters.addArgument(MONGODB_PASSWORD, "<password>");
    defaultParameters.addArgument(DBNAME, "<dbname>");
    defaultParameters.addArgument(OPERATION, "insert");
    defaultParameters.addArgument(COLLECTION, "<collection>");
    defaultParameters.addArgument(FILTER, "<filter>");
    defaultParameters.addArgument(DOCUMENT, "<document>");
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
      char[] password = context.getParameter(MONGODB_PASSWORD).toCharArray();

      MongoCredential credential = MongoCredential.createCredential(username, dbName, password);
      MongoClientSettings settings = MongoClientSettings.builder()
          .credential(credential)
          .applyToClusterSettings(builder ->
              builder.hosts(Collections.singletonList( new ServerAddress(hostname, port))).build())
          .build();

      mongoClient = MongoClients.create(settings);

      String collection = context.getParameter(COLLECTION);
      database = mongoClient.getDatabase(dbName);

      List<String> colNameList = new ArrayList<>();
      database.listCollectionNames().into(colNameList);
      if (!colNameList.contains(collection)) {
        log.error("Collection {} doesn't exist in database", collection);
        throw new IllegalArgumentException("Collection " + collection + " doesn't exist in Database" );
      }

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
    String collection = context.getParameter(COLLECTION);

    try {

      String result = null;
      if (INSERT_OPERATION.equalsIgnoreCase(context.getParameter(OPERATION))) {
        String document = context.getParameter(DOCUMENT);
        result = runInsertCommand(collection, document);
      } else if (QUERY_OPERATION.equalsIgnoreCase(context.getParameter(OPERATION))) {
        String filter = context.getParameter(FILTER);
        result = runQueryCommand(collection, filter);
      } else if (UPDATE_OPERATION.equalsIgnoreCase(context.getParameter(OPERATION))) {
        String document = context.getParameter(DOCUMENT);
        String filter = context.getParameter(FILTER);
        result = runUpdateCommand(collection, filter, document);
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

  private String runQueryCommand(String collection, String filter) {
    BasicDBObject mongoQuery = BasicDBObject.parse(filter);

    StringBuilder resultSB = new StringBuilder();
    FindIterable<Document> response = database.getCollection(collection).find(mongoQuery);
    response.forEach((Consumer<? super Document>) resDoc -> resultSB.append(resDoc.toJson()));

    return resultSB.toString();
  }

  private String runUpdateCommand(String collection, String filter,  String document) {
    BasicDBObject mongoFilter = BasicDBObject.parse(filter);
    Document mongoDocument = Document.parse(document);

    UpdateResult response = database.getCollection(collection).updateOne(mongoFilter, mongoDocument);

    return response.toString();
  }
}
