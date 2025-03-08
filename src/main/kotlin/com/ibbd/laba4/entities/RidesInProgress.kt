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
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_username")
    val client: Client,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fare_type")
    val fare: FareType,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_username")
    val driver: Driver,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
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
}