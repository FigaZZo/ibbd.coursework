package com.ibbd.laba4.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

class HowDidCodeEndedUpHere(override val message: String = "Em, oops!") : RuntimeException() {}
class InvalidDataInput(override val message: String = "Invalid data!") : RuntimeException(message) {}
class UniqueId(override val message: String = "Username already exists!") : RuntimeException(message) {}
class InternalServerError(override val message: String = "Internal server error") : RuntimeException(message) {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(HowDidCodeEndedUpHere::class)
    fun handleHowDidCodeEndedUpHere(e: HowDidCodeEndedUpHere) =
        ResponseEntity<String>(e.message, HttpStatus.INTERNAL_SERVER_ERROR)

    @ExceptionHandler(InvalidDataInput::class)
    fun handleInvalidDataInput(e: InvalidDataInput): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(UniqueId::class)
    fun handleUniqueId(e: UniqueId) = ResponseEntity<String>(e.message, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(InternalServerError::class)
    fun handleInternalServerError(e: InternalServerError) = ResponseEntity<String>(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
}