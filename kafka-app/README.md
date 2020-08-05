# Kafka Commands

## Run Zookeeper
```powershell
zookeeper-server-start config\zookeeper.properties
```

## Run Kakfa Server
```powershell
kafka-server-start config\server.properties
```

## Create Kafka Topic
```powershell
kafka-topics --zookeeper 127.0.0.1:2181 --topic userTopic --create --partitions 3 --replication-factor 1
```

## Create Kafka Consumer
```powershell
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic userTopic --group groupOne
```

## Kafka Topics
Cannot create topics with higher replication-factor than brokers
```powershell
kafka-topics --zookeeper 127.0.0.1:2181 --topic my_topic --create --partitions 3 --replication-factor 1
kafka-topics --zookeeper 127.0.0.1:2181 --list
kafka-topics --zookeeper 127.0.0.1:2181 --topic my_topic --describe
kafka-topics --zookeeper 127.0.0.1:2181 --topic my_topic --delete
```

## Kafka Producer
```powershell
kafka-console-producer --broker-list 127.0.0.1:9093 --topic first_topic
> Hello World
> Welcome to KAFKA

kafka-console-producer --broker-list 127.0.0.1:9093 --topic first_topic --producer-property acks=all
> Message 1
> Message 2
```

Get Only New Messages
```powershell
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic
```

Get All Messages + One's Yet To Arrive
```powershell
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning
```

## Kafka Producer Group Mode
Messages will be split between the consumers - Each consumer will read from different partition (Load Balanced)

#### Consumer 1
```powershell
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --group first-app-group
```
#### Consumer 2
```powershell
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --group first-app-group
```
#### Consumer 3
```powershell
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --group first-app-group
```

Other Options
```powershell
kafka-consumer-groups --bootstrap-server localhost:9092 --list
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group first-app-group
```

## Reset Offsets
Make consumer group replay all data which is already read
```powershell
kafka-consumer-groups --bootstrap-server localhost:9092 --group first-app-group --reset-offsets --to-earliest --execute --topic first_topic
```
Shift Forwards
```powershell
kafka-consumer-groups --bootstrap-server localhost:9092 --group first-app-group --reset-offsets --shift-by 2 --execute --topic first_topic
```

Shift Backwards
```powershell
kafka-consumer-groups --bootstrap-server localhost:9092 --group first-app-group --reset-offsets --shift-by -2 --execute --topic first_topic
```

## Configs
```powershell
kafka-topics --zookeeper 127.0.0.1:2181 --topic configuredTopic --create --partitions 3 --replication-factor 1
```
Help options
```powershell
kafka-configs
```

Show all Configs
```powershell
kafka-configs --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name usersTopic --describe
```

Add Config
```powershell
kafka-configs --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name usersTopic --add-config min.insync.replicas=2 --alter
```

Delete Config
```powershell
kafka-configs --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name usersTopic --delete-config min.insync.replicas=2 --alter
```
