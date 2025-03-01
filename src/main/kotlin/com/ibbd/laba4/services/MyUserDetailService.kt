package com.ibbd.laba4.services

import com.ibbd.laba4.entities.UserPrincipal
import com.ibbd.laba4.entities.UserWithPassword
import com.ibbd.laba4.repositories.ClientRepository
import com.ibbd.laba4.repositories.UserWithPasswordRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class MyUserDetailsService (
    private val userWithPasswordRepository: UserWithPasswordRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        // Load user from database or other source
        // Example:
        return userWithPasswordRepository.findByIdOrNull(username)
            ?.let {
                UserPrincipal(it)
            }
            ?: throw UsernameNotFoundException("User not found")
    }
}