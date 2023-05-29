# Replace "kafka-topics.sh" 
# by "kafka-topics" or "kafka-topics.bat" based on your system # (or bin/kafka-topics.sh or bin\windows\kafka-topics.bat if you didn't setup PATH / Environment variables)


############################
#####     LOCALHOST    #####
############################

kafka-topics

kafka-topics --bootstrap-server localhost:9092 --list

kafka-topics --bootstrap-server localhost:9092 --topic first-topic --create

kafka-topics --bootstrap-server localhost:9092 --topic second-topic --create --partitions 3

# If Broker is 1, Failed.
kafka-topics --bootstrap-server localhost:9092 --topic third-topic --create --partitions 3 --replication-factor 2

# Create a topic (working)
kafka-topics --bootstrap-server localhost:9092 --topic third-topic --create --partitions 3 --replication-factor 1

# List topics
kafka-topics --bootstrap-server localhost:9092 --list

# Describe a topic
kafka-topics --bootstrap-server localhost:9092 --topic first-topic --describe

# Delete a topic 
kafka-topics --bootstrap-server localhost:9092 --topic first-topic --delete
# (only works if delete.topic.enable=true)
