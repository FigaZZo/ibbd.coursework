package com.ibbd.laba4.repositories

import com.ibbd.laba4.entities.Car
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CarRepository: JpaRepository<Car, String> {
    fun findByDriverUsername(driverUsername: String): Car?
}