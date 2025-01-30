import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
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
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}
