plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "hello-kafka"
include("kafka-basic")
include("kafka-producer-wikimedia")
include("kafka-consumer-opensearch")
