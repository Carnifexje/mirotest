package com.example.mirotest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MirotestApplication {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            runApplication<MirotestApplication>(*args)
        }
    }
}