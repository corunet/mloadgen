# mloadgen

---

Mloadgen is a Mongo Document injector for MongoDB. It let you insert documents in the specified collection.

## Getting Started

---

MLoadGen had only a component which is a Java Request Sampler. It let you configure the connection to a mongo server define the collection and the document you wanto insert in.

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

Once build is completed, copy target/mloadgen-&gtversion>.jar file to JMETER_HOME/lib/ext directory. You also need to replace the mongo library version shipped with JMeter to at least the same defined in the pom.xml. Nowadays Jmeter version 5.2 use the version 2.11.3, which is quite old for this plugin.

### MLoadGenSampler

* **hostname** : Mongodb server hort name or Master server in a Mongo Cluster.
* **port** : Mongodb server port.
* **username** : User name allowed to insert documents.
* **password** : Password for the account.
* **dbname** : Data base name where collection is.
* **collection** : Collection where we want to insert documents.
* **document** : Document in JSON format we want to insert.

Document will be converte into BSON Document and inserted in the collection. Collection name is validated, if it is not in the server, the collection will not be created. Just to avoid duplicated collections and missdirections.

![MLoadGen Sampler](/Mloadgen_sampler.png)
