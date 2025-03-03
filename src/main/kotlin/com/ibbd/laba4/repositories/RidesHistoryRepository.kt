package com.ibbd.laba4.repositories

import com.ibbd.laba4.entities.RidesHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RidesHistoryRepository: JpaRepository<RidesHistory, Int> {
    fun findByClientUsername(username: String): RidesHistory?

    fun findByDriverUsername(driverUsername: String): RidesHistory?
}