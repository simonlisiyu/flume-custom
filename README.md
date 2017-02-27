# flume-custom
mvn clean install -DskipTests

## KafkaSink
flume evens: put header and body all to body, then push to kafka.

### KafkaSink example
collectorMainAgent.sinks.sink_kafka1.type = com.jcloud.flume.sink.kafka.KafkaSink
collectorMainAgent.sinks.sink_kafka1.topic = secure.log.topic
collectorMainAgent.sinks.sink_kafka1.brokerList = 172.27.36.57:9092,172.27.36.59:9092,172.27.36.60:9092
collectorMainAgent.sinks.sink_kafka1.requiredAcks = 1
collectorMainAgent.sinks.sink_kafka1.batchSize = 10000
#collectorMainAgent.sinks.sink_kafka1.type = logger

## TaildirMatcher
for a parent dir, recursive sub-dir, and get all files of suit rule.

### TaildirMatcher example
a1.sources.r1.type = com.jcloud.flume.source.taildir.TaildirSource
a1.sources.r1.positionFile = /var/log/flume/taildir_position.json
a1.sources.r1.filegroups = f1
a1.sources.r1.filegroups.f1 = /var/log/test/*.log
a1.sources.r1.headers.f1.headerKey1 = testlog
a1.sources.r1.fileHeader = true


## HeartBeatGeneratorSource

### HeartBeatGeneratorSource conf example
# Describe/configure the source
a1.sources.r1.type = com.jcloud.flume.source.heartbeat.HeartBeatGeneratorSource
a1.sources.r1.intervalMs = 60000

### cmd example
bin/flume-ng agent -c . -f conf/hb.conf -n a1 -Dflume.root.logger=INFO,console
