package com.ndgndg91.springkafkaconsumer.config

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory

class KafkaConsumerRebalanceHandler : ConsumerRebalanceListener {
    private val logger = LoggerFactory.getLogger(KafkaConsumerRebalanceHandler::class.java)
    override fun onPartitionsRevoked(partitions: MutableCollection<TopicPartition>) {
        logger.info("Partitions revoked: ${partitions.joinToString(", ") { it.partition().toString() }}")
    }

    override fun onPartitionsAssigned(partitions: MutableCollection<TopicPartition>) {
        logger.info("Partitions assigned: ${partitions.joinToString(", ") { it.partition().toString() }}")
    }
}
