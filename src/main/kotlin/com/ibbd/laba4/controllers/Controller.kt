package com.ibbd.laba4.controllers

import com.ibbd.laba4.entities.ClientDTO
import com.ibbd.laba4.entities.DriverDTO
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping

private val logger = KotlinLogging.logger {}

@Controller
@RequestMapping("/")
class Controller() {
    @ModelAttribute("client")
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

    @GetMapping(value = ["/client/login"])
    fun loginUser(): String{
        return "userLogin"
    }

    @GetMapping(value = ["/driver/login"])
    fun loginDriver(): String{
        return "driverLogin"
    }

    @GetMapping(value = ["/client/register"])
    fun userRegisterPage(): String {
        return "userRegister"
    }

    @GetMapping(value = ["/driver/register"])
    fun driverRegisterPage(): String {
        return "driverRegister"
    }
}