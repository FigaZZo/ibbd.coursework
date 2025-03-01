package com.ibbd.laba4.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "fares")
class FareType(
    @Id
    @Column(name = "fare_type")
    val fareType: String,
    @Column(name = "base_fare")
    val baseFare: Int,
    @Column(name = "cost_per_km")
    val costPerKm: Int,
) {
    constructor() : this("DefaultFareTypeName", 0, 0)
    constructor(fare: FareTypeDTO) : this(
        (fare.fareType ?: throw NullPointerException("FareType is invalid")),
        (fare.baseFare ?: throw NullPointerException("BaseFare is null")),
        (fare.costPerKm ?: throw NullPointerException("CostPerKm is null")),
    )
}

data class FareTypeDTO(
    var fareType: String? = null,
    var baseFare: Int? = null,
    var costPerKm: Int? = null,
) {
    override fun toString(): String {
        return "FareTypeDTO(faretype = $fareType, baseFare = $baseFare, costPerKm = $costPerKm)"
    }
}