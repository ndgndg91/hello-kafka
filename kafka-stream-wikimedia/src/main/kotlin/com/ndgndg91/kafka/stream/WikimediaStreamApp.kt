package com.ndgndg91.kafka.stream

import com.ndgndg91.kafka.stream.processor.BotCountStreamBuilder
import com.ndgndg91.kafka.stream.processor.EventCountTimeSeriesBuilder
import com.ndgndg91.kafka.stream.processor.WebsiteCountStreamBuilder
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

val logger: Logger = LoggerFactory.getLogger("main")
val properties = Properties().apply {
    put(StreamsConfig.APPLICATION_ID_CONFIG, "wikimedia-stats-application")
    put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass)
    put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().javaClass)
}
const val INPUT_TOPIC = "wikimedia.recentchange"

fun main() {
    val builder = StreamsBuilder()
    val changeJsonStream = builder.stream<String, String>(INPUT_TOPIC)

    val botCountStreamBuilder = BotCountStreamBuilder(changeJsonStream)
    botCountStreamBuilder.setup()

    val websiteCountStreamBuilder = WebsiteCountStreamBuilder(changeJsonStream)
    websiteCountStreamBuilder.setup()

    val eventCountTimeSeriesBuilder = EventCountTimeSeriesBuilder(changeJsonStream)
    eventCountTimeSeriesBuilder.setup()

    val appTopology = builder.build()
    logger.info("Topology: {}", appTopology.describe())
    val streams = KafkaStreams(appTopology, properties)
    streams.start()
}