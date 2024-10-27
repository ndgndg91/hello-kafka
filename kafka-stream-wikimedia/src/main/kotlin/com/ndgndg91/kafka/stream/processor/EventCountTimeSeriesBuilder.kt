package com.ndgndg91.kafka.stream.processor

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import java.time.Duration
import java.util.Map


class EventCountTimeSeriesBuilder(
    private var inputStream: KStream<String, String>
) {
    companion object{
        private val TIMESERIES_TOPIC: String = "wikimedia.stats.timeseries"
        private val TIMESERIES_STORE: String = "event-count-store"
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
    }


    fun setup() {
        val timeWindows = TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10))
        inputStream
            .selectKey { _: String?, _: String? -> "key-to-group" }
            .groupByKey()
            .windowedBy(timeWindows)
            .count(Materialized.`as`(TIMESERIES_STORE))
            .toStream()
            .mapValues { readOnlyKey: Windowed<String>, value: Long ->
                val kvMap = Map.of<String, Any>(
                    "start_time", readOnlyKey.window().startTime().toString(),
                    "end_time", readOnlyKey.window().endTime().toString(),
                    "window_size", timeWindows.size(),
                    "event_count", value
                )
                try {
                    return@mapValues OBJECT_MAPPER.writeValueAsString(kvMap)
                } catch (e: JsonProcessingException) {
                    return@mapValues null
                }
            }
            .to(
                TIMESERIES_TOPIC, Produced.with(
                    WindowedSerdes.timeWindowedSerdeFrom(String::class.java, timeWindows.size()),
                    Serdes.String()
                )
            )
    }
}