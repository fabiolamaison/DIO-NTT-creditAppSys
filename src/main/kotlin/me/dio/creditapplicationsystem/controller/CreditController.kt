package me.dio.creditapplicationsystem.controller

import jakarta.validation.Valid
import me.dio.creditapplicationsystem.dto.CreditDto
import me.dio.creditapplicationsystem.dto.CreditView
import me.dio.creditapplicationsystem.dto.CreditViewList
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.service.impl.CreditService
import me.dio.creditapplicationsystem.service.impl.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
class CreditController(
    private val creditService: CreditService,
    private val customerService: CustomerService
) {
    @PostMapping fun saveCredit(@RequestBody @Valid creditDto: CreditDto): ResponseEntity<String> {
        val credit: Credit = creditService.save(creditDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED
        ).body("Credit ${credit.creditCode} - ${credit.customer?.firstName} saved!")
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<CreditViewList>> {
        val customer: Customer? = customerService.findById(customerId)
        if (customer?.cpf!!.isEmpty()) {
            // Customer not found, return an error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
        val creditViewList = this.creditService.findAllByCustomer(customerId).stream().map {
            credit: Credit -> CreditViewList(credit)
        }.collect(Collectors.toList())
        return ResponseEntity.status(HttpStatus.OK).body(creditViewList)
    }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(@RequestParam(value = "customerId")customerId: Long,
                         @PathVariable creditCode: UUID
    ): ResponseEntity<CreditView> {
        val credit: Credit = creditService.findByCreditCode(customerId, creditCode)
        return ResponseEntity.status(HttpStatus.OK).body(CreditView(credit))
    }

}