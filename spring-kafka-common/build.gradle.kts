plugins {
    kotlin("jvm") version "1.9.25"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

group = "com.ndgndg91"

dependencies {
    api("io.cloudevents:cloudevents-kafka:4.0.1")
    api("io.cloudevents:cloudevents-json-jackson:4.0.1")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")
}
