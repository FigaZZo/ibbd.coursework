package com.ibbd.laba4.repositories

import org.springframework.data.jpa.repository.JpaRepository
import com.ibbd.laba4.entities.DriverSearch
import org.springframework.stereotype.Repository

@Repository
interface DriverSearchRepository: JpaRepository<DriverSearch, Int> {
    fun findAllByClientUsername(username: String): List<DriverSearch>
}