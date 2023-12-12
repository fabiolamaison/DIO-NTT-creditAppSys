package me.dio.creditapplicationsystem.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.util.constraint.MaxThreeMonths
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "Invalid Imput") val creditValue: BigDecimal,
    @field:Future(message = "Invalid Imput")
    @field:MaxThreeMonths() val dayFirstInstallment: LocalDate,
    @field:Min(value = 1, message = "Value must be greater than or equal to 1")
    @field:Max(value = 48, message = "Value must be less than or equal to 48")
    val numberOfInstallments: Int,
    @field:NotNull(message = "Invalid Imput") val customerId: Long
) {
    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
        )
}
