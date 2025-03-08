package com.ibbd.laba4.controllers

import com.ibbd.laba4.entities.*
import com.ibbd.laba4.entities.enums.Checkings
import com.ibbd.laba4.services.BasicService
import com.ibbd.laba4.services.JwtService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api")
class ApiController(
    private val basicService: BasicService,
    private val jwtService: JwtService
) {

    private fun getToken(request: HttpServletRequest): String {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            return authHeader.substring(7)
        }
        throw AccessDeniedException("Access denied")
    }

    private fun getCar(request: HttpServletRequest): String {
        return jwtService.extractPlateId(getToken(request))
    }

    @PostMapping(value = ["/fareType/add"])
    fun postFareType(
        @RequestBody fareType: FareTypeDTO,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<String> {
        if (userDetails.authorities.first().authority != "ROLE_ADMIN") throw AccessDeniedException("You are not allowed to access this resource")
        logger.info { "Adding fare type to the application." }

        fareType.run {
            val error = StringBuilder().also { sb ->
                Checkings.INT.check(baseFare)?.let { sb.append(it) }
                Checkings.STRING.check(this.fareType)?.let { sb.append(it) }
                Checkings.INT.check(costPerKm)?.let { sb.append(it) }
            }

            if (error.isNotEmpty()) {
                throw InvalidDataInput(error.toString())
            }
        }

        logger.info { "Fare type is $fareType, adding to the database." }

        return basicService.addFareType(FareType(fareType))
    }

    @GetMapping(
        value = [
            "/userTable",
            "/driverTable",
            "/driverSearchTable",
            "/ridesInProgressTable",
            "/ridesHistoryTable",
            "/fareTypeTable",
            "/carTable"
        ]
    )
    fun getSomething(request: HttpServletRequest, @AuthenticationPrincipal userDetails: UserDetails): Map<String, Any> {
        if (userDetails.authorities.first().authority != "ROLE_ADMIN") throw AccessDeniedException("You are not allowed to access this resource")

        return when (request.requestURI.substringAfter("/api")) {
            "/userTable" -> {
                basicService.getUsers()
            }

            "/driverTable" -> {
                basicService.getDrivers()
            }

            "/driverSearchTable" -> {
                basicService.getDriverSearches()
            }

            "/ridesInProgressTable" -> {
                basicService.getRidesInProgress()
            }

            "/ridesHistoryTable" -> {
                basicService.getRidesHistory()
            }

            "/fareTypeTable" -> {
                basicService.getFareTypes()
            }

            "/carTable" -> {
                basicService.getCars()
            }

            else -> {
                throw HowDidCodeEndedUpHere("getSomething error")
            }
        }
    }

    @GetMapping(value = ["/client/account"])
    fun accountPageUser(@AuthenticationPrincipal userDetails: UserDetails): Map<String, Any> {
        if (userDetails.authorities.first().authority == "ROLE_DRIVER") throw AccessDeniedException("Not allowed")
        logger.info { "${userDetails.username} requested accoung information" }

        return basicService.getClientPersonalInfo(userDetails.username)
    }

    @PostMapping(value = ["/client/callTaxi"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun callTaxi(
        @RequestBody taxi: DriverSearchDTO,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_DRIVER") throw AccessDeniedException("Not allowed")
        logger.info { "Client requested a taxi" }

        taxi.run {
            val error = StringBuilder().also { sb ->
                Checkings.USERNAME.check(userDetails.username)?.let { sb.append(it) }
                Checkings.STRING.check(fare)?.let { sb.append(it) }
//                Checkings.LOCATION.check(pickUpLocation)?.let { sb.append(it) }
//                Checkings.LOCATION.check(dropOffLocation)?.let { sb.append(it) }
            }

            if (error.isNotEmpty()) {
                throw InvalidDataInput(error.toString())
            }
        }
        logger.info { "Request to taxi is being processed\n$taxi" }

        return basicService.callTaxi(taxi.copy(client = userDetails.username))
    }

    @GetMapping(value = ["/client/rideInfo"])
    fun clientRideInfo(
        @AuthenticationPrincipal userDetails: UserDetails
    ): Map<String, Any> {
        if (userDetails.authorities.first().authority == "ROLE_DRIVER") throw AccessDeniedException("Not allowed")
        logger.info { "Request to rideInfo" }

        Checkings.USERNAME.check(userDetails.username)?.let { throw AccessDeniedException("You are not allowed to access this resource") }

        logger.info { "${userDetails.username} requested ride info" }

        return basicService.clientRideInfo(userDetails.username)
    }

    @GetMapping(value = ["/client/history"])
    fun clientHistory(
        @AuthenticationPrincipal userDetails: UserDetails
    ): Map<String, Any> {
        if (userDetails.authorities.first().authority == "ROLE_DRIVER") throw AccessDeniedException("Not allowed")
        logger.info { "Request to clientHistory" }

        Checkings.USERNAME.check(userDetails.username)?.let { throw AccessDeniedException("You are not allowed to access this resource") }

        logger.info { "${userDetails.username} requested history" }

        return basicService.clientHistory(userDetails.username)
    }

    @DeleteMapping(value = ["/client/cancelSearch"])
    fun cancelSearch(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_DRIVER") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "Client ${userDetails.username} canceled search" }

        return userDetails.username?.let { basicService.cancelSearch(it) } ?: throw InvalidDataInput("Invalid user")
    }

    @DeleteMapping(value = ["/client/cancelOrder"])
    fun cancelOrder(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_DRIVER") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "Client ${userDetails.username} canceled order" }

        return userDetails.username?.let { basicService.cancelOrderClient(it) } ?: throw InvalidDataInput("Invalid user")
    }



    @PostMapping(value = ["/driver/registerCar"])
    fun postCar(@RequestBody car: CarDTO, @AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        car.run {
            val error = StringBuilder().also { sb ->
                Checkings.PLATE.check(plate)?.let { sb.append(it) }
                Checkings.STRING.check(model)?.let { sb.append(it) }
                Checkings.STRING.check(color)?.let { sb.append(it) }
//                Checkings.LOCATION.check(location)?.let { sb.append(it) }
                Checkings.USERNAME.check(userDetails.username)?.let { sb.append(it) }
                Checkings.STRING.check(fare)?.let { sb.append(it) }
            }

            if (error.isNotEmpty()) {
                throw InvalidDataInput(error.toString())
            }
        }

        logger.info { "Registering new car for ${userDetails.username}\n$car" }

        return basicService.addCar(car.copy(driver = userDetails.username))
    }

    @PostMapping(value = ["/driver/account"])
    fun accountPageDriver(@AuthenticationPrincipal userDetails: UserDetails): Map<String, Any> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "Client ${userDetails.username} requested account info" }

        return basicService.getDriverPersonalInfo(userDetails.username)
    }

    @PostMapping(value = ["/driver/closeShift"])
    fun closeShiftDriver(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "${userDetails.username} ended shift" }

        return basicService.closeShift(userDetails.username)
    }

    @GetMapping(value = ["/driver/availableOrders"])
    fun getAvailableOrders(
        @AuthenticationPrincipal userDetails: UserDetails,
        request: HttpServletRequest
    ): Map<String, Any> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "${userDetails.username} requested available orders list" }

        return basicService.availableOrders(getCar(request))
    }

    @GetMapping(value = ["/driver/carsReport"])
    fun getCarsReport(@AuthenticationPrincipal userDetails: UserDetails): Map<String, Any> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "${userDetails.username} requested report about cars' usage" }

        return basicService.carsReport(userDetails.username)
    }

    @PostMapping(value = ["/driver/takeOrder"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun takeOrder(
        @RequestBody input: String,
        @AuthenticationPrincipal userDetails: UserDetails,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        val id = input.substringAfter(": ").filter {it in '0'..'9'}.toInt()

        logger.info { "${userDetails.username} has taken order - $id" }

        if (id != null && userDetails.username != null)
            return basicService.takeOrder(id, userDetails.username, getCar(request))
        else
            throw InvalidDataInput(if (id == null) "Invalid id" else "Invalid driver")
    }

    @PostMapping(value = ["/driver/cancelOrder"])
    fun cancelOrderDriver(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "${userDetails.username} canceled order" }

        return basicService.cancelOrderDriver(userDetails.username)
    }

    @PostMapping(value = ["/driver/arrived"])
    fun orderArrived(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "Driver ${userDetails.username} has arrived to the pick up point" }

        return userDetails.username?.let { basicService.carArrived(it) } ?: throw InvalidDataInput("Invalid driver")
    }

    @PostMapping(value = ["/driver/startRide"])
    fun rideStarted(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }

        logger.info { "Driver ${userDetails.username} has started ride" }

        return userDetails.username?.let { basicService.orderStartRide(it) } ?: throw InvalidDataInput("Invalid driver")
    }

    @PostMapping(value = ["/driver/completeRide"])
    fun rideCompleted(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody input: String
    ): ResponseEntity<String> {
        if (userDetails.authorities.first().authority == "ROLE_CLIENT") throw AccessDeniedException("Not allowed")

        val cost = input.substringAfter(": ").filter { it in '0'..'9' }.toInt()

        Checkings.USERNAME.check(userDetails.username)?.let { throw InvalidDataInput(it) }
        Checkings.INT.check(cost)?.let { throw InvalidDataInput(it) }

        logger.info { "Driver ${userDetails.username} has completed the ride" }

        if (userDetails.username != null && cost != null)
            return basicService.orderCompleted(userDetails.username, cost)
        else
            throw InvalidDataInput(if (userDetails.username == null) "Invalid driver" else "Invalid cost")
    }
}