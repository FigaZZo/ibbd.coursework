package com.ibbd.laba4.entities

import jakarta.persistence.*

@Entity
@Table(name = "rides_history")
class RidesHistory(
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "rides_history_seq",
        sequenceName = "rides_history_sequence",
        allocationSize = 10,
        initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rides_history_seq")
    val id: Int?,
    @Column(name = "pick_up_location")
    val pickUpLocation: String,
    @Column(name = "drop_off_location")
    val dropOffLocation: String,
    @Column(name = "pick_up_time")
    val pickUpTime: String,
    @Column(name = "drop_off_time")
    val dropOffTime: String,
    @Column(name = "status")
    val status: String,
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_username")
    val client: Client,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "driver_username")
    val driver: Driver?,
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "fare_type")
    val fare: FareType,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "plate_id")
    val car: Car?,
    @Column(name = "ride_cost")
    val cost: Int,
    @Column(name = "assessment_of_client")
    val assessmentOfClient: Int,
    @Column(name = "assessment_of_driver")
    val assessmentOfDriver: Int
) {
    constructor() : this(
        0,
        "DefaultPickUpLocationRidesHistory",
        "DefaultDropOffLocationRidesHistory",
        "DefaultPickUpTimeRidesHistory",
        "DefaultDropOffLocationRidesHistory",
        "DefaultStatusRidesHistory",
        Client(),
        Driver(),
        FareType(),
        Car(),
        0,
        0,
        0
    )
    constructor(ride: RidesHistoryDTO, client: Client, fare: FareType, driver: Driver, car: Car) : this(
        null,
        (ride.pickUpLocation?.let { "(${it.first}, ${it.second})" } ?: throw NullPointerException("Invalid pick up location")),
        (ride.dropOffLocation?.let { "(${it.first}, ${it.second})" } ?: throw NullPointerException("Invalid drop off location")),
        (ride.pickUpTime ?: throw NullPointerException("Invalid pick up time")),
        (ride.dropOffTime ?: throw NullPointerException("Invalid drop off time")),
        (ride.status ?: throw NullPointerException("Invalid status")),
        client,
        driver,
        fare,
        car,
        (ride.cost ?: throw NullPointerException("Invalid cost")),
        (ride.assessmentOfDriver ?: throw NullPointerException("Invalid assessment of driver")),
        (ride.assessmentOfClient ?: throw NullPointerException("Invalid assessment of client"))
    )
}

data class RidesHistoryDTO(
    var id: Int? = null,
    var pickUpLocation: Pair<Double, Double>? = null,
    var dropOffLocation: Pair<Double, Double>? = null,
    var pickUpTime: String? = null,
    var dropOffTime: String? = null,
    var status: String? = null,
    var client: String? = null,
    var driver: String? = null,
    var fare: String? = null,
    var car: String? = null,
    var cost: Int? = null,
    var assessmentOfClient: Int? = null,
    var assessmentOfDriver: Int? = null
) {
    override fun toString(): String {
        return "RidesHistoryDTO(id = $id, pickUpLocation = $pickUpLocation, dropOffLocation = $dropOffLocation, pickUpTime = $pickUpTime, dropOffTime = $dropOffTime, status = $status, user = $client, driver = $driver, car = $car, fare = $fare, cost = $cost, assessmentOfClient = $assessmentOfClient, assessmentOfDriver = $assessmentOfDriver)"
    }
}