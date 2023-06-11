################################
##                            ##
##        NO ZOOKEEPER        ##
##                            ##
################################

# create Kafka data directory
mkdir data/kafka-kraft

# Edit config/kraft/server.properties

# change lines to 
# log.dirs=/your/path/to/data/kafka
# example
# log.dirs=/Users/stephanemaarek/kafka_2.13-3.1.0/data/kafka-kraft


# generate a Kafka UUID
kafka-storage random-uuid

# This returns a UUID, for example 7Xqs5LXlTUCFwCPyb8xY3g
kafka-storage format -t <uuid> -c /usr/local/etc/kafka/kraft/server.properties
#Formatting /usr/local/var/lib/kraft-combined-logs with metadata.version 3.4-IV0.

# This will format the directory that is in the log.dirs in the config/kraft/server.properties file

# start Kafka
kafka-server-start /usr/local/etc/kafka/kraft/server.properties

# Kafka is running! 
# Keep the terminal window opened