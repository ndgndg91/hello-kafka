package com.ndgndg91.kafka.stream.processor

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import java.io.IOException
import java.time.Duration


class WebsiteCountStreamBuilder(
    private val inputStream : KStream<String, String>
) {
    companion object {
        private const val WEBSITE_COUNT_STORE: String = "website-count-store"
        private const val WEBSITE_COUNT_TOPIC: String = "wikimedia.stats.website"
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
    }

    fun setup() {
        val timeWindows = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1L))
        inputStream
            .selectKey { _: String, changeJson: String ->
                try {
                    val jsonNode: JsonNode = OBJECT_MAPPER.readTree(changeJson)
                    return@selectKey jsonNode["server_name"].asText()
                } catch (e: IOException) {
                    return@selectKey "parse-error"
                }
            }
            .groupByKey()
            .windowedBy(timeWindows)
            .count(Materialized.`as`(WEBSITE_COUNT_STORE))
            .toStream()
            .mapValues { key, value ->
                val kvMap = mapOf(
                    "website" to key.key(),
                    "count" to value
                )
                try {
                    OBJECT_MAPPER.writeValueAsString(kvMap)
                } catch (e: JsonProcessingException) {
                    null
                }
            }
            .to(
                WEBSITE_COUNT_TOPIC, Produced.with<Windowed<String>, String>(
                    WindowedSerdes.timeWindowedSerdeFrom(String::class.java, timeWindows.size()),
                    Serdes.String()
                )
            )
    }
}