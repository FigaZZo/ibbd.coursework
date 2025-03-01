package com.ibbd.laba4

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class Laba4Application

fun main(args: Array<String>) {

    runApplication<Laba4Application>(*args)
}
