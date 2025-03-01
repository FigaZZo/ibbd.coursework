package com.ibbd.laba4.controllers

import com.ibbd.laba4.entities.Client
import com.ibbd.laba4.entities.ClientDTO
import com.ibbd.laba4.entities.Driver
import com.ibbd.laba4.entities.DriverDTO
import com.ibbd.laba4.entities.enums.Checkings
import com.ibbd.laba4.services.BasicService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

private val logger = KotlinLogging.logger {}

@Controller
@RequestMapping("/")
class AuthenticationController(
    private val basicService: BasicService
) {
    @ModelAttribute("user")
    fun getUser(): ClientDTO {
        return ClientDTO(username = "", phoneNumber = "", name = "", password = "")
    }

    @ModelAttribute("driver")
    fun getDriver(): DriverDTO {
        return DriverDTO(username = "", phoneNumber = "", name = "", workExperience = 0, password = "")
    }

    @GetMapping(value = ["/login"])
    fun loginPage(): String {
        return "login"
    }

    @GetMapping(value = ["/user/login"])
    fun loginUser(): String{
        return "userLogin"
    }

    @GetMapping(value = ["/driver/login"])
    fun loginDriver(): String{
        return "driverLogin"
    }

    @GetMapping(value = ["/user/register"])
    fun userRegisterPage(): String {
        return "userRegister"
    }

    @GetMapping(value = ["/driver/register"])
    fun driverRegisterPage(): String {
        return "driverRegister"
    }

    @PostMapping(value = ["/driver/register"])
    fun postDriver(@ModelAttribute("driver") driver: DriverDTO, redirectAttributes: RedirectAttributes): String {
        logger.info { "Driver is $driver" }

        driver.run {
            val error = StringBuilder().also { sb ->
                Checkings.USERNAME.check(username)?.let { sb.append(it) }
                Checkings.PHONE_NUMBER.check(phoneNumber)?.let { sb.append(it) }
                Checkings.NAME.check(name)?.let { sb.append(it) }
                Checkings.RATING.check(rating)?.let { sb.append(it) }
                Checkings.WORK_EXPERIENCE.check(workExperience)?.let { sb.append(it) }
            }

            if (error.isNotEmpty()) {
                throw InvalidDataInput(error.toString())
            }
        }

        basicService.addDriver(Driver(driver))
        redirectAttributes.addAttribute("username", driver.username).addAttribute("password", driver.password)

        return "redirect:/defaultPage"
    }

    @PostMapping(value = ["/user/register"])
    fun postUser(@ModelAttribute("user") user: ClientDTO, redirectAttributes: RedirectAttributes): String {
        user.run {
            val error = StringBuilder().also { sb ->
                Checkings.USERNAME.check(username)?.let { sb.append(it) }
                Checkings.PHONE_NUMBER.check(phoneNumber)?.let { sb.append(it) }
                Checkings.NAME.check(name)?.let { sb.append(it) }
                Checkings.RATING.check(rating)?.let { sb.append(it) }
            }

            if (error.isNotEmpty()) {
                throw InvalidDataInput(error.toString())
            }
        }

        logger.info { "User is $user" }

        basicService.addUser(Client(user))

        redirectAttributes.addAttribute("username", user.username).addAttribute("password", user.password)
        return "redirect:/defaultPage"
    }

    @PostMapping(value = ["/user/login"])
    fun userLogin(
        @RequestParam username: String,
        @RequestParam password: String,
        redirectAttributes: RedirectAttributes
    ): String {
        redirectAttributes.addAttribute("username", username).addAttribute("password", password)
        return "redirect:/defaultPage"
    }

    @PostMapping(value = ["/driver/login"])
    fun driverLogin(
        @RequestParam username: String,
        @RequestParam password: String,
        redirectAttributes: RedirectAttributes
    ): String {
        redirectAttributes.addAttribute("username", username).addAttribute("password", password)
        return "redirect:/defaultPage"
    }

    @GetMapping(value = ["/defaultPage"])
    fun defaultPage(@RequestParam username: String, @RequestParam password: String): String {
        return "Hello, dear user. You token is ${basicService.verify(username, password)}"
    }
}