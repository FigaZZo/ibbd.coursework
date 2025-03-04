package com.ibbd.laba4.entities

import java.io.File

object EnvLoader {
    fun loadEnvFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.readLines().forEach { line ->
                val (key, value) = line.split("=", limit = 2)
                System.setProperty(key, value)
            }
        } else {
            throw RuntimeException("Environment file not found: $filePath")
        }
    }
}