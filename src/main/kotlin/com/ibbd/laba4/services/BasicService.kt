package com.ibbd.laba4.services

import com.ibbd.laba4.controllers.InternalServerError
import com.ibbd.laba4.controllers.UniqueId
import com.ibbd.laba4.repositories.*
import org.springframework.stereotype.Service
import com.ibbd.laba4.entities.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.Clock

private val logger = KotlinLogging.logger {}

private fun <T> List<T>.myNullCheck(): T? {
    if(this.isEmpty()) return null
    if(this.size > 1) throw InternalServerError("Database storage error")
    return this[0]
}

@Service
class BasicService(
    private val clientRepository: ClientRepository,
    private val driverRepository: DriverRepository,
    private val driverSearchRepository: DriverSearchRepository,
    private val ridesInProgressRepository: RidesInProgressRepository,
    private val ridesHistoryRepository: RidesHistoryRepository,
    private val fareTypeRepository: FareTypeRepository,
    private val carRepository: CarRepository,
    private val jwtService: JwtService,
    private val myUserDetailsService: MyUserDetailsService,
    private val authenticationManager: AuthenticationManager,
    private val userWithPasswordRepository: UserWithPasswordRepository
) {
    fun getUsers(): Map<String, Any> {
        return mapOf(
            "Users" to clientRepository.findAll()
        )
    }

    fun getDrivers(): Map<String, Any> {
        return mapOf(
            "Drivers" to driverRepository.findAll()
        )
    }

    fun getDriverSearches(): Map<String, Any> {
        return mapOf(
            "DriverSearches" to driverSearchRepository.findAll()
        )
    }

    fun getRidesInProgress(): Map<String, Any> {
        return mapOf(
            "RidesInProgress" to ridesInProgressRepository.findAll()
        )
    }

    fun getRidesHistory(): Map<String, Any> {
        return mapOf(
            "RidesHistory" to ridesHistoryRepository.findAll()
        )
    }

    fun getFareTypes(): Map<String, Any> {
        return mapOf(
            "FareTypes" to fareTypeRepository.findAll()
        )
    }

    fun getCars(): Map<String, Any> {
        return mapOf(
            "Cars" to carRepository.findAll()
        )
    }

    fun getClientPersonalInfo(username: String): Map<String, Any> {
        return mapOf(
            "Rides" to (ridesHistoryRepository.findByClientUsername(username) ?: "No rides"),
            "Personal info" to clientRepository.findById(username)
        )
    }

    fun getDriverPersonalInfo(username: String): Map<String, Any> {
        return mapOf(
            "Rides" to (ridesHistoryRepository.findByDriverUsername(username) ?: "No rides"),
            "Personal info" to driverRepository.findById(username),
            "Cars" to (carRepository.findByDriverUsername(username) ?: "No cars")
        )
    }

    fun addUser(client: Client, password: String, role: String): Unit {
        clientRepository.findByIdOrNull(client.username)
            ?.let {
                throw UniqueId("Username already exists")
            }
        clientRepository.findAllByPhoneNumber(client.phoneNumber)
            .myNullCheck()
            ?.let {
                throw UniqueId("Phone number already exists")
            }

        clientRepository.save(client)
        userWithPasswordRepository.save(UserWithPassword(client.username, password, role))
    }

    fun addDriver(driver: Driver, password: String, role: String): Unit {
        driverRepository.findByIdOrNull(driver.username)
            ?.let { throw UniqueId("Username already exists") }
        driverRepository.findAllByPhoneNumber(driver.phoneNumber)
            .myNullCheck()
            ?.let { throw UniqueId("Phone number already exists") }

        userWithPasswordRepository.save(UserWithPassword(driver.username, password, role))
        driverRepository.save(driver)
    }

    fun verify(username: String, password: String): String {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))

        return jwtService.generateToken(myUserDetailsService.loadUserByUsername(username))
    }

    fun addCar(car: CarDTO): ResponseEntity<String> {
        carRepository.findByIdOrNull(car.plate)
            ?.let { return ResponseEntity("Car already exists", HttpStatus.BAD_REQUEST) }

        val driver: Driver = driverRepository.findByIdOrNull(car.driver)
            ?: return ResponseEntity(
                "Driver not found for car",
                HttpStatus.NOT_FOUND
            )
        val fare: FareType = fareTypeRepository.findByIdOrNull(car.fare)
            ?: return ResponseEntity(
                "Fare not found for car",
                HttpStatus.NOT_FOUND
            )

        Car(car, driver, fare).also {
            carRepository.save(it)
        }

        return ResponseEntity(HttpStatus.ACCEPTED)
    }

    fun addFareType(fareType: FareType): ResponseEntity<String> {
        fareTypeRepository.save(fareType)

        return ResponseEntity(HttpStatus.ACCEPTED)
    }

    fun callTaxi(taxi: DriverSearchDTO): ResponseEntity<String> {
        logger.info { "Request: $taxi" }

        val client: Client = clientRepository.findByIdOrNull(taxi.client)
            ?: return ResponseEntity(
                "User not found for callTaxi",
                HttpStatus.NOT_FOUND
            )

        val fare: FareType = fareTypeRepository.findByIdOrNull(taxi.fare)
            ?: return ResponseEntity(
                "Fare not found for callTaxi",
                HttpStatus.NOT_FOUND
            )

        DriverSearch(taxi, client, fare).also {
            if (driverSearchRepository.existsByClientUsername(it.client.username) ||
                ridesInProgressRepository.existsByClientUsername(it.client.username)
            ) {
                return ResponseEntity("Client cannot call another taxi while having active order", HttpStatus.BAD_REQUEST)
            }
            driverSearchRepository.save(it)
        }

        return ResponseEntity(HttpStatus.ACCEPTED)
    }

    fun cancelSearch(username: String): ResponseEntity<String> {
        clientRepository.findByIdOrNull(username)
            ?.let { client ->
                driverSearchRepository.findAllByClientUsername(client.username).myNullCheck()
                    ?.let {
                        ridesHistoryRepository.save(
                            RidesHistory(
                                null,
                                it.pickUpLocation,
                                it.dropOffLocation,
                                "DefaultPickUpTimeRidesHistory",
                                "DefaultDropOffTiimeRidesHistory",
                                "Client canceled driver search",
                                client,
                                driver = null,
                                it.fare,
                                car = null,
                                0,
                                0,
                                0
                            )
                        )

                        driverSearchRepository.delete(it)
                    }
                    ?: return ResponseEntity(
                        "User does not has pending order",
                        HttpStatus.BAD_REQUEST
                    )
            }
            ?: return ResponseEntity(
                "User not found for cancelTaxi",
                HttpStatus.NOT_FOUND
            )


        return ResponseEntity("Client canceled order while searching for driver", HttpStatus.OK)
    }

    fun cancelOrderClient(clientUsername: String): ResponseEntity<String> {
        clientRepository.findByIdOrNull(clientUsername)
            ?.let { client ->
                ridesInProgressRepository.findAllByClientUsername(client.username).myNullCheck()
                    ?.let {
                        ridesHistoryRepository.save(
                            RidesHistory(
                                null,
                                it.pickUpLocation,
                                it.dropOffLocation,
                                "DefaultPickUpTimeRidesHistory",
                                "DefaultDropOffTiimeRidesHistory",
                                "Client canceled order",
                                client,
                                it.driver,
                                it.fare,
                                it.car,
                                0,
                                0,
                                0
                            )
                        )

                        ridesInProgressRepository.delete(it)
                    }
                    ?: return ResponseEntity(
                        "User does not has pending order",
                        HttpStatus.BAD_REQUEST
                    )
            }
            ?: return ResponseEntity(
                "User not found for cancelTaxi",
                HttpStatus.NOT_FOUND
            )


        return ResponseEntity("Client canceled order while searching for driver", HttpStatus.OK)
    }

    fun takeOrder(id: Int, driverUsername: String, plateId: String): ResponseEntity<String> {
        val driver = driverRepository.findByIdOrNull(driverUsername)
            ?.also { driver ->
                ridesInProgressRepository.findAllByClientUsername(driver.username).myNullCheck()
                    ?.let {
                        return ResponseEntity("Driver already has order in takeOrder", HttpStatus.BAD_REQUEST)
                    }
            }
            ?: return ResponseEntity("Driver not found for takeOrder", HttpStatus.NOT_FOUND)

        driverSearchRepository.findByIdOrNull(id)
            ?.let { curDriverSearch ->
                if (ridesInProgressRepository.existsByClientUsername(curDriverSearch.client.username)) {
                    return ResponseEntity("Somebody has already taken order", HttpStatus.BAD_REQUEST)
                }

                ridesInProgressRepository
                    .save(
                        RidesInProgress(
                            null,
                            curDriverSearch.pickUpLocation,
                            curDriverSearch.dropOffLocation,
                            "Waiting for driver",
                            Clock.systemUTC().instant().toString(),
                            curDriverSearch.client,
                            curDriverSearch.fare,
                            driver,
                            carRepository.findByIdOrNull(plateId) ?: return ResponseEntity(
                                "No car found for driver in takeOrder",
                                HttpStatus.NOT_FOUND
                            )
                        )
                    )

                driverSearchRepository.delete(curDriverSearch)
            }
            ?: return ResponseEntity("Order not found for takeOrder", HttpStatus.NOT_FOUND)

        return ResponseEntity(HttpStatus.OK)
    }

    fun carArrived(driverUsername: String): ResponseEntity<String> {
        val driver = driverRepository.findByIdOrNull(driverUsername)
            ?: return ResponseEntity("Driver not found for carArrived", HttpStatus.NOT_FOUND)

        ridesInProgressRepository.findAllByDriverUsername(driver.username).myNullCheck()
            ?.let { curRideInProgress ->
                curRideInProgress.status = "Driver is waiting"
            }
            ?: return ResponseEntity("No ride found in carArrived", HttpStatus.NOT_FOUND)


        return ResponseEntity(HttpStatus.OK)
    }

    fun orderStartRide(driverUsername: String): ResponseEntity<String> {
        val driver = driverRepository.findByIdOrNull(driverUsername)
            ?: return ResponseEntity("Driver not found for orderStartRide", HttpStatus.NOT_FOUND)

        ridesInProgressRepository.findAllByDriverUsername(driver.username).myNullCheck()
            ?.let { curRideInProgress ->
                curRideInProgress.status = "Client is in the car, ride is in progress"
            }
            ?: return ResponseEntity("No ride found for orderStartRide", HttpStatus.NOT_FOUND)

        return ResponseEntity(HttpStatus.OK)
    }

    fun orderCompleted(driverUsername: String, cost: Int): ResponseEntity<String> {
        val driver = driverRepository.findByIdOrNull(driverUsername)
            ?: return ResponseEntity("Driver not found for orderCompleted", HttpStatus.NOT_FOUND)

        ridesInProgressRepository.findAllByDriverUsername(driver.username).myNullCheck()
            ?.let { curRideInProgress ->
                ridesHistoryRepository.save(
                    RidesHistory(
                        null,
                        curRideInProgress.pickUpLocation,
                        curRideInProgress.dropOffLocation,
                        curRideInProgress.pickUpTime,
                        Clock.systemUTC().instant().toString(),
                        "Ride is completed successfully",
                        curRideInProgress.client,
                        curRideInProgress.driver,
                        curRideInProgress.fare,
                        curRideInProgress.car,
                        cost,
                        5,
                        5
                    )
                )

                ridesInProgressRepository.delete(curRideInProgress)
            }
            ?: return ResponseEntity("No ride found for orderCompleted", HttpStatus.BAD_REQUEST)

        return ResponseEntity(HttpStatus.OK)
    }

    fun cancelOrderDriver(driverUsername: String): ResponseEntity<String> {
        driverRepository.findByIdOrNull(driverUsername)
            ?.let { driver ->
                ridesInProgressRepository.findAllByDriverUsername(driver.username).myNullCheck()
                    ?.let {
                        ridesHistoryRepository.save(
                            RidesHistory(
                                null,
                                it.pickUpLocation,
                                it.dropOffLocation,
                                it.pickUpTime,
                                "DefaultDropOffTiimeRidesHistory",
                                "Client canceled driver search",
                                it.client,
                                driver = null,
                                it.fare,
                                it.car,
                                0,
                                0,
                                0
                            )
                        )

                        ridesInProgressRepository.delete(it)
                    }
                    ?: return ResponseEntity(
                        "Driver does not has pending order",
                        HttpStatus.BAD_REQUEST
                    )
            }
            ?: return ResponseEntity(
                "Driver was not found for cancelTaxiDriver",
                HttpStatus.NOT_FOUND
            )


        return ResponseEntity("Driver canceled order while getting to the client's location", HttpStatus.OK)
    }

    fun availableOrders(plateId: String): Map<String, Any> {
        carRepository.findByIdOrNull(plateId)
            ?.let{
                return mapOf(
                    "AvailabaleOrders" to driverSearchRepository.findAllByFare(it.fare)
                )
            }
            ?: throw NoSuchElementException("No available order found for $plateId")
    }

    fun startShift(driverUsername: String, plateId: String): String {
        driverRepository.findByIdOrNull(driverUsername)
            ?: throw UsernameNotFoundException("Driver $driverUsername not found")

        return jwtService.generateToken(myUserDetailsService.loadUserByUsername(driverUsername), plateId)
    }

    fun closeShift(driverUsername: String): ResponseEntity<String> {
        driverRepository.findByIdOrNull(driverUsername)
            ?: return ResponseEntity("Driver not found for $driverUsername", HttpStatus.NOT_FOUND)
        return ResponseEntity("Shift is finished", HttpStatus.OK)
    }
}