package com.ndgndg91.kafka.stream.processor

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import java.io.IOException


class BotCountStreamBuilder(private var inputStream: KStream<String, String>) {
    companion object {
        private const val BOT_COUNT_STORE: String = "bot-count-store"
        private const val BOT_COUNT_TOPIC: String = "wikimedia.stats.bots"
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
    }

    fun setup() {
        inputStream.mapValues { changeJson: String? ->
            try {
                val jsonNode = OBJECT_MAPPER.readTree(changeJson)
                if (jsonNode["bot"].asBoolean()) {
                    return@mapValues "bot"
                }
                return@mapValues "non-bot"
            } catch (e: IOException) {
                return@mapValues "parse-error"
            }
        }
            .groupBy { _: String, botOrNot: String -> botOrNot }
            .count(Materialized.`as`<String, Long, KeyValueStore<Bytes, ByteArray>>(BOT_COUNT_STORE))
            .toStream()
            .mapValues<String> { key: String, value: Long ->
                val kvMap = mapOf(key to value)
                try {
                    return@mapValues OBJECT_MAPPER.writeValueAsString(kvMap)
                } catch (e: JsonProcessingException) {
                    return@mapValues null
                }
            }
            .to(BOT_COUNT_TOPIC)
    }
}