package me.dio.creditapplicationsystem.entity

import jakarta.persistence.Embeddable

@Embeddable
data class Address(
    var zipcode: String = "",
    var street: String = ""
)
