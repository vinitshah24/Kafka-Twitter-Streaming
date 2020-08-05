# Kafka Twitter Streaming App

* Kafka Producer to stream the live tweets and feeding it into the Kafka cluster.
* Kafka Consumer to consume the data and feed it into the ElasticSearch database.

## Instructions

Start Zookeeper
```powershell
zookeeper-server-start config\zookeeper.properties
```

Start Kafka Server
```powershell
kafka-server-start config\server.properties
```

Start ElasticSearch Server
```powershell
elasticsearch
```

### Create Kafka Topic
```powershell
kafka-topics --zookeeper 127.0.0.1:2181 --topic twitterApp --create --partitions 6 --replication-factor 1
```