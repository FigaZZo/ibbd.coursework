package com.ibbd.laba4.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "driver")
class Driver(
    @Id
    @Column(name = "username")
    val username: String,
    @Column(name = "phone_number")
    val phoneNumber: String,
    @Column(name = "secondname")
    val name: String,
    @Column(name = "work_experience")
    val workExperience: Int,
    @Column(name = "rating")
    val rating: Double
) {
    constructor() : this("DefaultUsernameDriver", "DefaultPhoneNumberDriver", "DefaultNameDriver", 0, 5.0)
    constructor(driver: DriverDTO) : this(
        (driver.username ?: throw NullPointerException("Username is null")),
        (driver.phoneNumber?.filter { it.isDigit() } ?: throw NullPointerException("PhoneNumber is null")),
        (driver.name ?: throw NullPointerException("Name is null")),
        (driver.workExperience ?: throw NullPointerException("WorkExperience is null")),
        (driver.rating ?: 5.0)
    )
}

data class DriverDTO(
    var username: String? = null,
    var phoneNumber: String? = null,
    var name: String? = null,
    var workExperience: Int? = null,
    var rating: Double? = null,
    var password: String? = null
) {
    override fun toString(): String {
        return "DriverDTO(username=$username, phoneNumber=$phoneNumber, name=$name, workExperience=$workExperience, rating=$rating)"
    }
}