package com.ibbd.laba4.entities

import jakarta.persistence.*

@Entity
@Table(name = "driver_search")
class DriverSearch(
    @Id
    @SequenceGenerator(name = "driver_search_seq",
        sequenceName = "driver_search_sequence",
        initialValue = 1,
        allocationSize = 10)
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_search_seq")
    val id: Int? = null,
    @Column(name = "pick_up_location")
    val pickUpLocation: String,
    @Column(name = "drop_off_location")
    val dropOffLocation: String,
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "user_username")
    val client: Client,
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "fare_type")
    val fare: FareType
) {
    constructor() : this(
        0,
        "DefaultPickUpLocationDriverSearch",
        "DefaultDropOffLocationDriverSearch",
        Client(),
        FareType()
    )
    constructor(driver: DriverSearchDTO, client: Client, fare: FareType) : this(
        null,
        driver.pickUpLocation?.let{ "(${it.first}, ${it.second})" } ?: throw NullPointerException("Invalid pick up location"),
        driver.dropOffLocation?.let{ "(${it.first}, ${it.second})" } ?: throw NullPointerException("Invalid drop off location"),
        client,
        fare
    ) {}
}

data class DriverSearchDTO(
    var pickUpLocation: Pair<Double, Double>? = null,
    var dropOffLocation: Pair<Double, Double>? = null,
    var client: String? = null,
    var fare: String? = null,
    var id: Int? = null
)