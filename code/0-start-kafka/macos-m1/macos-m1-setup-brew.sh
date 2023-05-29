#################################
#### DOWNLOAD KAFKA BINARIES ####
#################################

# Install Brew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Kafka (will install Java if needed)
brew install kafka

# Try out a Kafka command (no .sh needed)
kafka-topics

# Homebrew will install Kafka under /opt/homebrew/bin, but the files will be linked into other directories:
#  Binaries and scripts will be in /opt/homebrew/bin
#  Kafka configurations will be in /opt/homebrew/etc/kafka
#  Zookeeper configuration will be in /opt/homebrew/etc/zookeeper
#  The log.dirs config (the location for Kafka data) will be set to /opt/homebrew/var/lib/kafka-logs

# Start Zookeeper
/opt/homebrew/bin/zookeeper-server-start /opt/homebrew/etc/zookeeper/zoo.cfg

# Start Kafka
/opt/homebrew/bin/kafka-server-start /opt/homebrew/etc/kafka/server.properties