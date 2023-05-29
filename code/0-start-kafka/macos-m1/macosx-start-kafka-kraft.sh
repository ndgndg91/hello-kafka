################################
##                            ##
##        NO ZOOKEEPER        ##
##                            ##
################################

# generate a Kafka UUID
kafka-storage random-uuid

# This returns a UUID, for example 76BLQI7sT_ql1mBfKsOk9Q
kafka-storage format -t <uuid> -c /opt/homebrew/etc/kafka/kraft/server.properties
# Formatting /opt/homebrew/var/lib/kraft-combined-logs with metadata.version 3.4-IV0.

# This will format the directory that is in the log.dirs in the config/kraft/server.properties file

# start Kafka
kafka-server-start /opt/homebrew/etc/kafka/kraft/server.properties

# Kafka is running! 
# Keep the terminal window opened