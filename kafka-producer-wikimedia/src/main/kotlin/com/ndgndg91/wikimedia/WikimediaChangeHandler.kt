package com.ndgndg91.wikimedia

import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory

class WikimediaChangeHandler(
    private val kafkaProducer: KafkaProducer<String, String>,
    private val topic: String
): EventHandler{
    private val logger = LoggerFactory.getLogger(WikimediaChangeHandler::class.java)

    override fun onOpen() {
        // nothing
    }

    override fun onClosed() {
        kafkaProducer.close()
    }

    override fun onMessage(event: String, messageEvent: MessageEvent) {
        logger.info(messageEvent.data)
        // async
        kafkaProducer.send(ProducerRecord(topic, messageEvent.data))
    }

    override fun onComment(comment: String?) {
        // nothing
    }

    override fun onError(e: Throwable?) {
        logger.error("error in stream reading", e)
    }
}