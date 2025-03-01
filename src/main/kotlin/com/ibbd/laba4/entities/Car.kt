package com.ibbd.laba4.entities

import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
@Table(name = "car")
class Car(
    @Id
    @Column(name = "plate_id")
    val plate: String,
    @Column(name = "model")
    val model: String,
    @Column(name = "color")
    val color: String,
    @Column(name = "location")
    val location: String,
//    @Column(name = "status")
//    @Enumerated(EnumType.STRING)
//    var status: CarStatus,
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_username")
    val driver: Driver,
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "fare_type")
    val fare: FareType
) {
    constructor() : this(
        "DefaultPlateCar", "DefaultModelCar", "DefaultColorCar", "DefaultLocationCar", Driver(), FareType()
    )
    constructor(car: CarDTO, driver: Driver, fare: FareType) : this(
        (car.plate ?: throw NullPointerException("Plate is null")),
        (car.model ?: throw NullPointerException("Model is null")),
        (car.color ?: throw NullPointerException("Color is null")),
        (car.location?.let{ "(${it.first}, ${it.second})" } ?: throw NullPointerException("Location is null")),
        driver,
        fare
    )
}

data class CarDTO(
    var plate: String? = null,
    var model: String? = null,
    var color: String? = null,
    var location: Pair<Double, Double>? = null,
    var driver: String? = null,
    var fare: String? = null
) {
    override fun toString(): String {
        return "CarDTO(plate = $plate, model = $model, color = $color, location = $location, driver = $driver, fare = $fare)"
    }
}