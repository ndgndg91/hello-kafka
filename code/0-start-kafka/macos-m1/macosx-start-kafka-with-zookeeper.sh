################################
##                            ##
##       WITH ZOOKEEPER       ##
##                            ##
################################


# Start Zookeeper
/opt/homebrew/bin/zookeeper-server-start /opt/homebrew/etc/zookeeper/zoo.cfg

# Start Kafka
/opt/homebrew/bin/kafka-server-start /opt/homebrew/etc/kafka/server.properties

# Kafka is running! 
# Keep the two terminal windows opened