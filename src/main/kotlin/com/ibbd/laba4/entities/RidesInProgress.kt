package com.ibbd.laba4.entities

import jakarta.persistence.*

@Entity
@Table(name = "rides_in_progress")
class RidesInProgress(
    @Id
    @Column(name = "id")
    @SequenceGenerator(
        name = "rides_in_progress_seq",
        sequenceName = "rides_in_progress_sequence",
        allocationSize = 10,
        initialValue = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rides_in_progress_seq")
    var id: Int?,
    @Column(name = "pick_up_location", columnDefinition = "point")
    val pickUpLocation: String,
    @Column(name = "drop_off_location", columnDefinition = "point")
    val dropOffLocation: String,
    @Column(name = "status")
    var status: String,
    @Column(name = "pick_up_time", columnDefinition = "timestamp")
    val pickUpTime: String,
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_username")
    val client: Client,
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fare_type")
    val fare: FareType,
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_username")
    val driver: Driver,
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_plate_id")
    val car: Car
) {
    constructor() : this(
        0,
        "DefaultPickUpLocationRidesInProgress",
        "DefaultDropOffLocationRidesInProgress",
        "DefaultStatusRidesInProgress",
        "DefaultPickUpTimeRidesInProgress",
        Client(),
        FareType(),
        Driver(),
        Car()
    )

    constructor(ride: RidesInProgressDTO) : this(
        null,
        (ride.pickUpLocation?.let { "(${it.first}, ${it.second})" }
            ?: throw NullPointerException("Invalid pick up location")),
        (ride.dropOffLocation?.let { "(${it.first}, ${it.second})" }
            ?: throw NullPointerException("Invalid drop off location")),
        (ride.status ?: throw NullPointerException("Invalid status")),
        (ride.pickUpTime ?: throw NullPointerException("Invalid pick up time")),
        (ride.client ?: throw NullPointerException("Invalid user")),
        (ride.fare ?: throw NullPointerException("Invalid fareType")),
        (ride.driver ?: throw NullPointerException("Invalid driver name")),
        (ride.car ?: throw NullPointerException("Invalid car"))
    )
}

data class RidesInProgressDTO(
    var id: Int? = null,
    var pickUpLocation: Pair<Double, Double>? = null,
    var dropOffLocation: Pair<Double, Double>? = null,
    var status: String,
    var pickUpTime: String? = null,
    var client: Client? = null,
    var fare: FareType? = null,
    var driver: Driver? = null,
    var car: Car? = null
) {
    override fun toString(): String {
        return "RidesInProgressDTO(id = $id, pickUpLocation = $pickUpLocation, dropOffLocation = $dropOffLocation, status = $status, pickUpTime = $pickUpTime, user = $client, fare = $fare, driver = $driver, car = $car)"
    }
}