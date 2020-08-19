package com.example.mirotest.controllers

import com.example.mirotest.exceptions.WidgetNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ControllerErrorHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(WidgetNotFoundException::class)
    fun handleWidgetNotFoundException(ex: WidgetNotFoundException): ResponseEntity<Any> {
        log.debug("Returning not found. Reason: {}", ex.message)
        return ResponseEntity.notFound().build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ControllerErrorHandler::class.java)
    }
}