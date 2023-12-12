package me.dio.creditapplicationsystem.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.creditapplicationsystem.entity.Adress
import me.dio.creditapplicationsystem.entity.Customer
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDto(
    @field:NotEmpty(message = "Invalid Imput") val firstName: String,
    @field:NotEmpty(message = "Invalid Imput") val lastName: String,
    @field:CPF(message = "This Invalid CPF") val cpf: String,
    @field:NotNull(message = "Invalid Imput") val income: BigDecimal,
    @field:Email(message = "Invalid E-mail") val email: String,
    @field:NotEmpty(message = "Invalid Imput") val password: String,
    @field:NotEmpty(message = "Invalid Imput") val zipcode: String,
    @field:NotEmpty(message = "Invalid Imput") val street: String
    ) {

    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        income = this.income,
        email = this.email,
        password = this.password,
        adress = Adress(
            zipcode = this.zipcode,
            street = this.street
            )
    )

}
