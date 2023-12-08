package me.dio.creditapplicationsystem.entity

import jakarta.persistence.*

@Entity

@Table(name = "Cliente")
data class Customer(
    @Column(nullable = false) var firstName: String = "",
    @Column(nullable = false)var lastName: String = "",
    @Column(nullable = false, unique = true)val cpf: String,
    @Column(nullable = false)var email: String = "",
    @Column(nullable = false)var password: String = "",
    @Column(nullable = false) @Embedded var adress: Adress = Adress(),
    @Column(nullable = false) @OneToMany(fetch = FetchType.LAZY,
        cascade = [CascadeType.REMOVE, CascadeType.PERSIST],
    mappedBy = "customer")
    var credit: List<Credit> = mutableListOf(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)val id: Long? = null,
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
                "CPF=$cpf, e-mail=$email, adress=$adress" +
                "password=$password, credits=$credit)"
    }
}
