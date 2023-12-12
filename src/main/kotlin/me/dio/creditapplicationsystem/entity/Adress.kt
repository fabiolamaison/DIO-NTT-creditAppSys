package me.dio.creditapplicationsystem.entity

import jakarta.persistence.Embeddable

@Embeddable
data class Adress(
    var zipcode: String = "",
    var street: String = ""
)
