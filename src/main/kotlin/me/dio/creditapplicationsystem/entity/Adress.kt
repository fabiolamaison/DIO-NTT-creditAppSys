package me.dio.creditapplicationsystem.entity

import jakarta.persistence.Embeddable

@Embeddable
data class Adress(
    var zipCode: String = "",
    var street: String = ""
)
