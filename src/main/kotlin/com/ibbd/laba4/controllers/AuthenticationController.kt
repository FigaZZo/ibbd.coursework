package com.ibbd.laba4.controllers

import com.ibbd.laba4.entities.Client
import com.ibbd.laba4.entities.ClientDTO
import com.ibbd.laba4.entities.Driver
import com.ibbd.laba4.entities.DriverDTO
import com.ibbd.laba4.entities.enums.Checkings
import com.ibbd.laba4.services.BasicService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class AuthenticationController(
    private val basicService: BasicService,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping(value = ["/driver/register"])
    fun postDriver(@ModelAttribute("driver") driver: DriverDTO): ResponseEntity<String> {

        driver.run {
            val error = StringBuilder().also { sb ->
                Checkings.USERNAME.check(username)?.let { sb.append(it) }
                Checkings.PHONE_NUMBER.check(phoneNumber)?.let { sb.append(it) }
                Checkings.NAME.check(name)?.let { sb.append(it) }
                Checkings.STRING.check(password)?.let { sb.append(it) }
                Checkings.WORK_EXPERIENCE.check(workExperience)?.let { sb.append(it) }
            }

            if (error.isNotEmpty()) {
                throw InvalidDataInput(error.toString())
            }
        }

        logger.info { "New $driver is being registered" }

        basicService.addDriver(Driver(driver), passwordEncoder.encode(driver.password), "ROLE_DRIVER")
        basicService.verify(driver.username!!, driver.password!!).also {
            return ResponseEntity.ok()
                .header("Authorization", "Bearer:$it")
                .body("Welcome")
        }
    }

    @PostMapping(value = ["/client/register"])
    fun postUser(@ModelAttribute("client") client: ClientDTO): ResponseEntity<String> {
        client.run {
            val error = StringBuilder().also { sb ->
                Checkings.USERNAME.check(username)?.let { sb.append(it) }
                Checkings.PHONE_NUMBER.check(phoneNumber)?.let { sb.append(it) }
                Checkings.NAME.check(name)?.let { sb.append(it) }
                Checkings.STRING.check(password)?.let { sb.append(it) }
            }

            if (error.isNotEmpty()) {
                throw InvalidDataInput(error.toString())
            }
        }

        logger.info { "New $client is being registered" }


        basicService.addUser(Client(client), passwordEncoder.encode(client.password), "ROLE_CLIENT")
        basicService.verify(client.username!!, client.password!!).also {
            return ResponseEntity.ok()
                .header("Authorization", "Bearer:$it")
                .body("Welcome")
        }
    }

    @PostMapping(value = ["/client/login"])
    fun userLogin(
        @RequestParam username: String,
        @RequestParam password: String
    ): ResponseEntity<String> {
        logger.info { "Login request received for $username" }

        basicService.verify(username, password).also {
            return ResponseEntity.ok()
                .header("Authorization", "Bearer:$it")
                .body("Welcome")
        }
    }

    @PostMapping(value = ["/driver/login"])
    fun driverLogin(
        @RequestParam username: String,
        @RequestParam password: String
    ): ResponseEntity<String> {
        logger.info { "Login request received for $username" }

        basicService.verify(username, password).also {
            return ResponseEntity.ok()
                .header("Authorization", "Bearer:$it")
                .body("Welcome")
        }
    }

    @GetMapping(value = ["/defaultPage"])
    fun defaultPage(request: HttpServletRequest): String {
        request.getHeader("Authorization")
            ?.let {
                val token = it.substringAfter("Bearer:")
                return "Hello, dear client. Your token is $token"
            }
            ?: throw InvalidDataInput("The default page is invalid")
    }
}