# MLoadGen - JSON Schema/JSchema + Mongo Load Generator

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/556dc69dff5643ad855a7323c8442876)](https://app.codacy.com/gh/corunet/mloadgen?utm_source=github.com&utm_medium=referral&utm_content=corunet/mloadgen&utm_campaign=Badge_Grade)
[![Build Status](https://api.travis-ci.org/corunet/mloadgen.svg?branch=master)](https://travis-ci.org/corunet/mloadgen)

---

Mloadgen is a Mongo Document injector for MongoDB. It let you insert documents in the specified collection.

## Getting Started

---

MLoadGen had only a component which is a Java Request Sampler. It let you configure the connection to a mongo server define the collection and the document you want to insert in.

### Setup

---

#### Requirement

MLoadGen uses Java, hence on JMeter machine JRE 8 or superior:

Install openjdk on Debian, Ubuntu, etc.,

```bash

 sudo apt-get install openjdk-8-jdk
```

Install openjdk on Fedora, Oracle Linux, Red Hat Enterprise Linux, etc.,

```bash
 su -c "yum install java-1.8.0-openjdk-devel"
```

For windows and mac and you can:

* download oracle JDK 8 setup from [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* using chocolatey (windows):
        <https://chocolatey.org/packages?q=java>
   brew (mac):

```bash
 brew tap adoptopenjdk/openjdk
 brew cask install adoptopenjdk8
```

#### Build Project

```bash
 mvn clean install
```

Once build is complete, copy target/mloadgen-&gtversion>.jar file to JMETER_HOME/lib/ext directory. You also need to replace the mongo library version shipped with JMeter to at least the same defined in the pom.xml. Nowadays Jmeter version 5.2 use the version 2.11.3, which is quite old for this plugin and if you try to connect the modern mongo cluster.

Other change required to get it work, is to replace the Mongo Java Driver for anyone higher than version 3.1.0. Driver can be found in <JMETER HOME>/lib/
The latest available, and tested, when this Readme was wrote was the 3.12.7 you can found it [here](https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/3.12.7/mongo-java-driver-3.12.7.jar)

## Components

### MLoadGenSampler

  * **hostname** : Mongodb server hort name or Master server in a Mongo Cluster.
  * **port** : Mongodb server port.
  * **username** : User name allowed to insert documents.
  * **password** : Password for the account.
  * **dbname** : Data base name where collection is.
  * **operation** : Operation to execute insert/update/delete/query.
  * **collection** : Collection where we want to insert documents.
  * **document** : Document in JSON format we want to insert.

Document will be converted into BSON Document and inserted in the collection. The collection name is beaing validated, if it is not in the server, the collection will not be created. Just to avoid duplicated collections and missdirections.

![MLoadGen Sampler](/Mloadgen_sampler.png)

### File Load Generator Config

This component allows to upload a Json Schema, or a JSchema, file to generate the artificial data to inject in the Mongo DB.

  * **Collection name** : Name of the Collection where the data will be inserted
  * **Schema Type** : Type of the schema to process, JSchema or JSON Schema
  * **Field Value Table** : Table which contains the flattened structure of the schema plus the values to configure the generation.

We will see 4 columns where we will configure the Random Generator system.

  * **Field Name** : Flattened field name compose by all the properties from the root class. Ex: PropClass1.PropClass2.ProrpClass3 **Note**: In case to be an array [] will appear at the end. If you want to define a specific size for the array just type the number.
  * **Field Type** : Field type, like String, Int, Double, Array **Note** : if the field is an array of basic types it will be show as string-array, int-array,...
  * **Field Length** : Field length configuration for the Random Tool. In case of an String mean the number of characters, in case of a Number the number of digits.
  * **Field Values List** : Field possibles values which will be used by the Random Tool to generate values.

 **Note** In "Field Type" if the field type is an array or a map you can define a specific number of random values(metadata.extensions.flows[].correlation[2]).
              In "Field Values List" if the field type is an array or a map you can define a specific list of values([1,2,3,4,5] or [ key1:value1, key2:value2, key3:value3]).

![Load Generator Table](/File_Load_Generator_Config.png)

### MLoadGen Schema Sampler

Java Sampler who manage the connection against the mongo cluster and data injection.

  * **hostname** : Mongodb server hort name or Master server in a Mongo Cluster.
  * **port** : Mongodb server port.
  * **username** : User name allowed to insert documents.
  * **password** : Password for the account.
  * **url** : URl connection against a MongoDB Cluster.
  * **operation** : Operation to execute insert/update.

![MLoadGen Schema Sampler](/Java_Request_-_MLoadGen_Gen_Schema_Sampler.png)
