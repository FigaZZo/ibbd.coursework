package com.ibbd.laba4.repositories

import com.ibbd.laba4.entities.UserWithPassword
import org.springframework.data.jpa.repository.JpaRepository

interface UserWithPasswordRepository: JpaRepository<UserWithPassword, String> {
}