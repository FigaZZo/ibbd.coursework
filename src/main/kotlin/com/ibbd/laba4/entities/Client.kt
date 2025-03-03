package com.ibbd.laba4.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class Client(
    @Id
    @Column(name = "username")
    val username: String,
    @Column(name = "phone_number")
    val phoneNumber: String,
    @Column(name = "secondname")
    val name: String,
    @Column(name = "rating")
    val rating: Double
) {
    constructor() : this("DefaultUsernameUser", "DefaultPhoneNumberUser", "DefaultNameUser", 5.0) {}
    constructor(clientDTO: ClientDTO) : this(
        (clientDTO.username ?: throw NullPointerException("Username is null")),
        (clientDTO.phoneNumber?.filter { it.isDigit() } ?: throw NullPointerException("PhoneNumber is null")),
        (clientDTO.name ?: throw NullPointerException("Name is null")),
        (clientDTO.rating ?: 5.0),
    )
}

data class ClientDTO(
    var username: String? = null,
    var phoneNumber: String? = null,
    var name: String? = null,
    var rating: Double? = null,
    var password: String? = null
) {
    override fun toString(): String {
        return "UserDTO(username=$username, phoneNumber=$phoneNumber, name=$name, rating=$rating, password=$password)"
    }
}