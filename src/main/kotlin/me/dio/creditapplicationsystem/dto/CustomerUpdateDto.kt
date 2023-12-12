package me.dio.creditapplicationsystem.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.creditapplicationsystem.entity.Customer
import java.math.BigDecimal

data class CustomerUpdateDto(
    @field:NotEmpty(message = "Invalid Imput") val firstName: String,
    @field:NotEmpty(message = "Invalid Imput") val lastName: String,
    @field:NotNull(message = "Invalid Imput") val income: BigDecimal,
    @field:NotEmpty(message = "Invalid Imput") val zipcode: String,
    @field:NotEmpty(message = "Invalid Imput")val street: String
) {
    fun toEntity(customer: Customer) : Customer{
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.adress.zipcode = this.zipcode
        customer.adress.street = this.street
        return customer
    }
}
