package me.dio.creditapplicationsystem.service.impl

import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.repository.CustomerRepository
import me.dio.creditapplicationsystem.service.ICreditService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerRepository: CustomerRepository
): ICreditService {
    override fun save(credit: Credit): Credit {
        credit.apply {
            this.customer = customerRepository.findById(credit.customer?.id!!).orElse(null)
        }
        if (credit.customer != null) {
            return creditRepository.save(credit)
        } else {
            throw RuntimeException("Invalid customer ID")
        }
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> {
        return creditRepository.findAllByCustomerId(customerId)
    }

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit: Credit = (this.creditRepository.findByCreditCode(creditCode) ?:
        throw RuntimeException("Creditcode $creditCode not found"))
        return if(credit.customer?.id!! == customerId) credit else throw IllegalArgumentException("Contact admin")
    }
}
