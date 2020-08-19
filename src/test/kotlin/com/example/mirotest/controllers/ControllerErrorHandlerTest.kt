package com.example.mirotest.controllers

import com.example.mirotest.exceptions.WidgetNotFoundException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus
import java.util.*

internal class ControllerErrorHandlerTest {

    private val handler = ControllerErrorHandler()

    @Test
    fun handleWidgetNotFoundException() {
        val actual = handler.handleWidgetNotFoundException(
                WidgetNotFoundException(UUID.randomUUID())
        )
        Assertions.assertThat(actual.statusCode).isEqualByComparingTo(HttpStatus.NOT_FOUND)
    }
}