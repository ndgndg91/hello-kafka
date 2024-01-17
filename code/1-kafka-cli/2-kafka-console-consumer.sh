# Replace "kafka-console-consumer"
# by "kafka-console-consumer" or "kafka-console-consumer.bat" based on your system # (or bin/kafka-console-consumerlocalhost or bin\windows\kafka-console-consumer.bat if you didn't setup PATH / Environment variables)

kafka-console-consumer

############################
#####     LOCALHOST    #####
############################

# create a topic with 3 partitions
kafka-topics --bootstrap-server localhost:9092 --topic second-topic --create --partitions 3

# consuming
kafka-console-consumer --bootstrap-server localhost:9092 --topic second-topic

# other terminal
kafka-console-producer --bootstrap-server localhost:9092 \
--producer-property partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner \
--topic second-topic

# consuming from beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic second-topic --from-beginning

# display key, values and timestamp in consumer
kafka-console-consumer --bootstrap-server localhost:9092 --topic second-topic --formatter \
kafka.tools.DefaultMessageFormatter --property print.timestamp=true --property print.key=true \
--property print.value=true --property print.partition=true --from-beginning