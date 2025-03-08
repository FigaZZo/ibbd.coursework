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
}