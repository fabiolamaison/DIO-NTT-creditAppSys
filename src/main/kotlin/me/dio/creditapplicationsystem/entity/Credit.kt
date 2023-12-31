package me.dio.creditapplicationsystem.entity

import jakarta.persistence.*
import me.dio.creditapplicationsystem.enumerate.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity

@Table(name = "credit")
data class Credit(
    @Column(nullable = false, unique = true) var creditCode: UUID = UUID.randomUUID(),
    @Column(nullable = false) val creditValue: BigDecimal =  BigDecimal.ZERO,
    @Column(nullable = false) val dayFirstInstallment: LocalDate,
    @Column(nullable = false) val numberOfInstallments: Int = 0,
    @Enumerated val status: Status = Status.IN_PROGRESS,
    // declarar como var garante segurança?
    @ManyToOne var customer: Customer? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
) {
    // other methods...


    // Functions override recomended by the IDE to improve performance

    // Used and functional
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Credit) return false

        return id == other.id
    }

    //Not yet used
    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    //Not yet used
    override fun toString(): String {
        return "Credit(id=$id, creditCode=$creditCode, creditValue=$creditValue, " +
                "dayFirstInstallment=$dayFirstInstallment, numberOfInstallments=$numberOfInstallments, " +
                "status=$status, customer=$customer)"
    }
}
