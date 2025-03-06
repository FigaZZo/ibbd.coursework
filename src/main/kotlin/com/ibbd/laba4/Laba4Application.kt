package com.ibbd.laba4

import com.ibbd.laba4.entities.EnvLoader
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class Laba4Application

val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {

    logger.info { "Getting environment variables" }
    EnvLoader.loadEnvFile("src/main/resources/static/envFile.txt")

    runApplication<Laba4Application>(*args)
    logger.info { "Application hast started" }
}