package com.ibbd.laba4.repositories

import com.ibbd.laba4.entities.Driver
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DriverRepository : JpaRepository<Driver, String> {
    fun findAllByPhoneNumber(phone: String): List<Driver>
}