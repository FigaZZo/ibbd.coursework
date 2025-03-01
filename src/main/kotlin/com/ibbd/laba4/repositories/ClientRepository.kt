package com.ibbd.laba4.repositories

import com.ibbd.laba4.entities.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository: JpaRepository<Client, String> {
    fun findAllByPhoneNumber(phone: String): List<Client>
}