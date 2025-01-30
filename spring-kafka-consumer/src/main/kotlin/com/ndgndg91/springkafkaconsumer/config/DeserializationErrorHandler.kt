package com.ndgndg91.springkafkaconsumer.config

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.errors.RecordDeserializationException
import org.springframework.kafka.listener.ConsumerRecordRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.util.backoff.BackOff

/**
 * RecordDeserializationException 발생 시 offset 을 1개 넘어가고 commit 하도록 처리
 */
class DeserializationErrorHandler(
    recoverer: ConsumerRecordRecoverer, backOff: BackOff
): DefaultErrorHandler(recoverer, backOff) {

    override fun handleOtherException(
        thrownException: Exception,
        consumer: Consumer<*, *>,
        container: MessageListenerContainer,
        batchListener: Boolean
    ) {
        if (thrownException is RecordDeserializationException) {
            logger.error(thrownException, "${thrownException.message}")
            consumer.seek(thrownException.topicPartition(), thrownException.offset() + 1L)
            consumer.commitSync()
        } else {
            throw IllegalStateException("This error handler cannot process '" + thrownException.javaClass.getName() + "'s; no record information is available", thrownException)
        }
    }
}