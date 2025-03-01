package com.ibbd.laba4.repositories

import com.ibbd.laba4.entities.FareType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FareTypeRepository: JpaRepository<FareType, String>