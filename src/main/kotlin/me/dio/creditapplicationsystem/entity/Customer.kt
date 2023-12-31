package me.dio.creditapplicationsystem.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity

@Table(name = "customer")
data class Customer(
    @Column(nullable = false) var firstName: String = "",
    @Column(nullable = false)var lastName: String = "",
    @Column(nullable = false, unique = true) var cpf: String = "",
    @Column(nullable = false) var email: String = "",
    @Column(nullable = false) var password: String = "",
    @Column(nullable = false) var income: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false) @Embedded var address: Address = Address(),
    @Column(nullable = false) @OneToMany(fetch = FetchType.LAZY,
        cascade = [CascadeType.REMOVE, CascadeType.PERSIST],
    mappedBy = "customer")
    var credit: List<Credit> = mutableListOf(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
)  {
    // other methods...

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Credit) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Customer(id=$id, firstName=$firstName, lastName=$lastName, " +
                "CPF=$cpf, e-mail=$email, adress=$address" +
                "password=$password, credits=$credit)"
    }

}
