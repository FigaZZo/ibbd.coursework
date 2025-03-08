package com.ibbd.laba4.repositories

import com.ibbd.laba4.entities.RidesHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RidesHistoryRepository: JpaRepository<RidesHistory, Int> {
    fun findAllByClientUsername(username: String): List<RidesHistory>

    fun countByCarPlate(carPlate: String): Int

    fun findAllByCarPlate(carPlate: String): List<RidesHistory>
}