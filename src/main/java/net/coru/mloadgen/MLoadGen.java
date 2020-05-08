package net.coru.mloadgen;

import static net.coru.mloadgen.MLoadGenConfigHelper.COLLECTION;
import static net.coru.mloadgen.MLoadGenConfigHelper.DBNAME;
import static net.coru.mloadgen.MLoadGenConfigHelper.DOCUMENT;
import static net.coru.mloadgen.MLoadGenConfigHelper.MONGODB_HOSTNAME;
import static net.coru.mloadgen.MLoadGenConfigHelper.MONGODB_PASSWORD;
import static net.coru.mloadgen.MLoadGenConfigHelper.MONGODB_PORT;
import static net.coru.mloadgen.MLoadGenConfigHelper.MONGODB_USERNAME;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    defaultParameters.addArgument(COLLECTION, "<collection>");
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
    String dbName = context.getParameter(DBNAME);
    String collection = context.getParameter(COLLECTION);
    String document = context.getParameter(DOCUMENT);

    MongoDatabase database = mongoClient.getDatabase(dbName);

    Document mongoDocument = Document.parse(document);
    try {
      database.getCollection(collection).insertOne(mongoDocument);
      sampleResult.setResponseData(document, StandardCharsets.UTF_8.name());
      sampleResult.setSuccessful(true);
      log.info("Following document had been inserted in the collection {} : {}", collection, document);
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
}
