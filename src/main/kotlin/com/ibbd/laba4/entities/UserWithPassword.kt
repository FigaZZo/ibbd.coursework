package com.ibbd.laba4.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_password")
class UserWithPassword(
    @Id
    @Column(name = "username")
    val username: String = "DefaultUsernameUserWithPassword",
    @Column(name = "password")
    val password: String = "DefaultPasswordUserWithPassword",
    @Column(name = "role")
    val role: String = "DefaultRoleUserWithPassword"
) {

}