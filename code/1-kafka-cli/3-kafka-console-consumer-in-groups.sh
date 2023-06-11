# Replace "kafka-console-consumer" 
# by "kafka-console-consumer" or "kafka-console-consumer.bat" based on your system # (or bin/kafka-console-consumer or bin\windows\kafka-console-consumer.bat if you didn't setup PATH / Environment variables)

############################
#####     LOCALHOST    #####
############################

# create a topic with 3 partitions
kafka-topics --bootstrap-server localhost:9092 --topic third-topic --create --partitions 3

# start one consumer
kafka-console-consumer --bootstrap-server localhost:9092 --topic third-topic --group my-first-application

# start one producer and start producing
kafka-console-producer --bootstrap-server localhost:9092 --producer-property partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner --topic third-topic

# start another consumer part of the same group. See messages being spread
kafka-console-consumer --bootstrap-server localhost:9092 --topic third-topic --group my-first-application

# start another consumer part of a different group from beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic third-topic --group my-second-application --from-beginning