package com.example.mirotest.controllers

import com.example.mirotest.ApplicationConfig
import com.example.mirotest.MirotestApplication
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [MirotestApplication::class, ApplicationConfig::class])
@AutoConfigureMockMvc
internal class OpenApiGeneratorTest @Autowired constructor(val mockMvc: MockMvc, val objectMapper: ObjectMapper) {

    @LocalServerPort
    var port: Long = 0

    @Test
    @Throws(Exception::class)
    fun generateOpenApiSpec() {
        val response = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:$port/v3/api-docs"))
                .andReturn()
                .response
                .contentAsByteArray
        val yml = YAMLMapper().writeValueAsBytes(objectMapper.readTree(response))
        Files.write(Paths.get("target" + File.separator + "openapi.yml"), yml)
    }
}