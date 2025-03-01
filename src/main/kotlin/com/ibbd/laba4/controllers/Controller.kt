package com.ibbd.laba4.controllers

import com.ibbd.laba4.entities.*
import com.ibbd.laba4.entities.enums.Checkings
import com.ibbd.laba4.services.BasicService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

/*
driver:
    register
    login
    registerCar
    account:
        myCars
        driver
        myRides
    startShift
    closeShift
    openOrders
    takeOrder
    cancelOrder
    arrived
    startRide
    completeRide
user:
    register
    login
    account:
        rides
        user
    callTaxi
    cancelTaxi
faretype:
    add


 */

@RestController
@RequestMapping("/api")
class Controller(private val basicService: BasicService) {

    @PostMapping(
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
    fun getSomething(request: HttpServletRequest): Map<String, Any> {
        return when (request.requestURI) {
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

    @PostMapping(value = ["/user/account"])
    fun accountPageUser() {

    }

    @PostMapping(value = ["/user/callTaxi"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun callTaxi(@RequestBody taxi: DriverSearchDTO): ResponseEntity<String> {
        logger.info { "Client requested taxi: $taxi" }

        taxi.run {
            val error = StringBuilder().also { sb ->
                Checkings.USERNAME.check(client)?.let { sb.append(it) }
                Checkings.STRING.check(fare)?.let { sb.append(it) }
                Checkings.LOCATION.check(pickUpLocation)?.let { sb.append(it) }
                Checkings.LOCATION.check(dropOffLocation)?.let { sb.append(it) }
            }

            if (error.isNotEmpty()) {
                throw InvalidDataInput(error.toString())
            }
        }

        return basicService.callTaxi(taxi)
    }

    @DeleteMapping(value = ["/user/cancelTaxi"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun cancelTaxi(@RequestBody user: String?): ResponseEntity<String> {
        logger.info { "Client canceled taxi $user" }

        Checkings.USERNAME.check(user)?.let { throw InvalidDataInput(it) }

        return user?.let { basicService.cancelOrder(it) } ?: throw InvalidDataInput("Invalid user")
    }

    @PostMapping(value = ["/driver/registerCar"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun postCar(@RequestBody car: CarDTO): ResponseEntity<String> {
        logger.info { "Car is $car" }
        car.run {
            val error = StringBuilder().also { sb ->
                Checkings.PLATE.check(plate)?.let { sb.append(it) }
                Checkings.STRING.check(model)?.let { sb.append(it) }
                Checkings.STRING.check(color)?.let { sb.append(it) }
                Checkings.LOCATION.check(location)?.let { sb.append(it) }
                Checkings.USERNAME.check(driver)?.let { sb.append(it) }
                Checkings.STRING.check(fare)?.let { sb.append(it) }
            }

            if (error.isNotEmpty()) {
                throw InvalidDataInput(error.toString())
            }
        }

        return basicService.addCar(car)
    }

    @PostMapping(value = ["/driver/account"])
    fun accountPageDriver() {
        //TODO("get account info")
    }

    @PostMapping(value = ["/driver/startShift"])
    fun startShiftDriver() {
        //TODO("func to start shift")
    }

    @PostMapping(value = ["/driver/closeShift"])
    fun closeShiftDriver() {
        //TODO("func to close shift")
    }

    @PostMapping(value = ["/driver/availableOrders"])
    fun getAvailableOrders() {
        //TODO("func to get list of available orders")
    }

    @PostMapping(value = ["/driver/takeOrder"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun takeOrder(@RequestBody id: Int?, @RequestBody driver: String?): ResponseEntity<String> {
        logger.info { "$driver is taking order $id" }

        Checkings.USERNAME.check(driver)?.let { throw InvalidDataInput(it) }

        if (id != null && driver != null)
            return basicService.takeOrder(id, driver)
        else
            throw InvalidDataInput(if (id == null) "Invalid id" else "Invalid driver")
    }

    @PostMapping(value = ["/driver/cancelOrder"])
    fun cancelOrderDriver() {
        //TODO("func to cancel order by driver")
    }

    @PostMapping(value = ["/driver/arrived"])
    fun orderArrived(@RequestBody driver: String?): ResponseEntity<String> {
        logger.info { "Driver has arrived $driver" }

        Checkings.USERNAME.check(driver)?.let { throw InvalidDataInput(it) }

        return driver?.let { basicService.carArrived(it) } ?: throw InvalidDataInput("Invalid driver")
    }

    @PostMapping(value = ["/driver/startRide"])
    fun rideStarted(@RequestBody driver: String?): ResponseEntity<String> {
        logger.info { "Driver has started ride $driver" }

        Checkings.USERNAME.check(driver)?.let { throw InvalidDataInput(it) }

        return driver?.let { basicService.orderStartRide(it) } ?: throw InvalidDataInput("Invalid driver")
    }

    @PostMapping(value = ["/driver/completeRide"])
    fun rideCompleted(@RequestBody driver: String?, @RequestBody cost: Int?): ResponseEntity<String> {
        logger.info { "Driver has completed the ride $driver" }

        Checkings.USERNAME.check(driver)?.let { throw InvalidDataInput(it) }
        Checkings.INT.check(cost)?.let { throw InvalidDataInput(it) }

        if(driver != null && cost != null)
            return basicService.orderCompleted(driver, cost)
        else
            throw InvalidDataInput(if(driver == null) "Invalid driver" else "Invalid cost")
    }

    @PostMapping(value = ["/fareType/add"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun postFareType(@RequestBody fareType: FareTypeDTO): ResponseEntity<String> {
        logger.info { "Fare type is $fareType" }
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

        return basicService.addFareType(FareType(fareType))
    }
}