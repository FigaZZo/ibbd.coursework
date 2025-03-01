package com.ibbd.laba4.entities

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(
    private val user: UserWithPassword
): UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?>? = listOf(SimpleGrantedAuthority(user.role))

    override fun getPassword(): String? = user.password

    override fun getUsername(): String? = user.username
}