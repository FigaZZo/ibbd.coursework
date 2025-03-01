package com.ibbd.laba4.repositories

import org.springframework.data.jpa.repository.JpaRepository
import com.ibbd.laba4.entities.RidesInProgress
import org.springframework.stereotype.Repository

@Repository
interface RidesInProgressRepository: JpaRepository<RidesInProgress, Int> {
    fun findAllByClientUsername(username: String): List<RidesInProgress>

    fun findAllByDriverUsername(driverUsername: String): List<RidesInProgress>
}