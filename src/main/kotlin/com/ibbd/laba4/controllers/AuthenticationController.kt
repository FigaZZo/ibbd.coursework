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
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestBody

private val logger = KotlinLogging.logger {}

@Controller
class AuthenticationController(
    private val basicService: BasicService,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping(value = ["/driver/register"])
    fun postDriver(
        @ModelAttribute("driver") driver: DriverDTO,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Unit {

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
            request.session.setAttribute("token", it)
            request.session.setAttribute("role", "ROLE_DRIVER")
        }

        logger.info { "Redirecting to defaultPage" }
        response.sendRedirect("/defaultPage")
    }

    @PostMapping(value = ["/client/register"])
    fun postUser(
        @ModelAttribute("client") client: ClientDTO,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Unit {
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
            request.session.setAttribute("token", it)
            request.session.setAttribute("role", "ROLE_CLIENT")
        }

        logger.info { "Redirecting to defaultPage" }
        response.sendRedirect("/defaultPage")
    }

    @PostMapping(value = ["/client/login"])
    fun userLogin(
        @RequestParam username: String,
        @RequestParam password: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Unit {
        logger.info { "Login request received for $username" }

        basicService.verify(username, password).also {
            request.session.setAttribute("token", it)
            request.session.setAttribute("role", "ROLE_CLIENT")
        }

        logger.info { "Redirecting to defaultPage" }
        response.sendRedirect("/defaultPage")
    }

    @PostMapping(value = ["/driver/login"])
    fun driverLogin(
        @RequestParam username: String,
        @RequestParam password: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Unit {
        logger.info { "Login request received for $username" }

        basicService.verify(username, password).also {
            request.session.setAttribute("token", it)
            request.session.setAttribute("role", "ROLE_DRIVER")
        }

        logger.info { "Redirecting to defaultPage" }
        response.sendRedirect("/defaultPage")
    }

    @GetMapping(value = ["/defaultPage"])
    fun defaultPage(request: HttpServletRequest, model: Model): String {
        val token = request.session.getAttribute("token") as String
        val role = request.session.getAttribute("role") as String

        model.addAttribute("token", token)
        model.addAttribute("role", role)

        return "defaultPage"
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        request.session.invalidate()

        response.sendRedirect("/login")
    }

    @PostMapping(value = ["/api/driver/startShift"])
    fun startShiftDriver(
        @RequestBody input: String?,
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        val plateId = input?.substringAfter(": ")?.filter { if(it in 'А'..'Я' || it in '0'..'9') true else false } ?: throw InvalidDataInput("Invalid plate")

        logger.info { "Hey ${plateId}" }

        Checkings.PLATE.check(plateId)?.let { throw InvalidDataInput(it) }
        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "${userDetails.username} started shift" }

        val token = basicService.startShift(userDetails.username, plateId)
        model.addAttribute("token2", token)

        return ResponseEntity.ok()
            .header("Authorization", "Bearer $token")
            .body("Shift started")
    }
}