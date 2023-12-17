package me.dio.creditapplicationsystem.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.impl.annotations.InjectMockKs
import me.dio.creditapplicationsystem.dto.CreditDto
import me.dio.creditapplicationsystem.dto.CustomerDto
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.exception.ExceptionDetails
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.repository.CustomerRepository
import me.dio.creditapplicationsystem.service.impl.CreditService
import me.dio.creditapplicationsystem.service.impl.CustomerService
import me.dio.creditapplicationsystem.util.DateValidationUtils
import me.dio.creditapplicationsystem.utils.ThisSysTestUtils
import org.hamcrest.Matchers.*
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.isEqualTo
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditControllerTest {
    @Autowired
    lateinit var customerRepository: CustomerRepository
    @Autowired
    lateinit var creditRepository: CreditRepository
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should save credit through valid customer id and return status 200`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val creditAsString: String = objectMapper.writeValueAsString(buildCreditDto(customerId = customer.id!!))
        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(creditAsString))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should return credit through valid credit UUID and customer Id end returning status 200`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerId: Long = customer.id!!
        val credit: Credit = buildCreditDto(customerId = customerId).toEntity()
        credit.creditCode = java.util.UUID.fromString("17d65d06-955e-42da-ad66-4deb3bff83bd")
        creditRepository.save(credit)
        //when
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customerId=$customerId")
            .accept(MediaType.APPLICATION_JSON)
        )   ///then
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should NOT return credit due to invalid credit UUID and return status 404`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerId: Long = customer.id!!
        val credit: Credit = buildCreditDto(customerId = customerId).toEntity()
        val invalidUUID: UUID = ThisSysTestUtils.uuidOtherThan(credit.creditCode)
        //when
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidUUID?customerId=$customerId")
            .accept(MediaType.APPLICATION_JSON)
        )   //then
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should NOT return credit due to incompatible credit UUID and customer ID end returning status 409`(){
        //given
        val customer1: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customer2: Customer = customerRepository.save(
            buildCustomerDto(cpf = "790.789.450-60", email = "string@mail.com").toEntity())
        val creditCustomer1: Credit = creditRepository.save(
            buildCreditDto(customerId = customer1.id!!).toEntity())
        //when
        mockMvc.perform(
            MockMvcRequestBuilders.get(
            "$URL/${creditCustomer1.creditCode}?customerId=${customer2.id}")
            .accept(MediaType.APPLICATION_JSON)
        )   //then
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should return a list of credits based on valid customer id and return status 200`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerId: Long = customer.id!!
        val credit1: Credit = creditRepository.save(buildCreditDto(customerId = customerId).toEntity())
        val credit2: Credit = creditRepository.save(buildCreditDto(customerId = customerId).toEntity())
        val credit3: Credit = creditRepository.save(buildCreditDto(customerId = customerId).toEntity())
        //when
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=$customerId")
            .accept(MediaType.APPLICATION_JSON)
        )   //then
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should NOT return a list of credits based on invalid customer id and return status 404`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerId: Long = customer.id!!
        val invalidId: Long = ThisSysTestUtils.idOtherThan(customer.id!!)
        val credit1: Credit = creditRepository.save(buildCreditDto(customerId = customerId).toEntity())
        val credit2: Credit = creditRepository.save(buildCreditDto(customerId = customerId).toEntity())
        val credit3: Credit = creditRepository.save(buildCreditDto(customerId = customerId).toEntity())
        //when
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=$invalidId")
            .accept(MediaType.APPLICATION_JSON)
        )   //then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should NOT save credit due to date value and return status 400`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerId: Long = customer.id!!
        val invalidDate = LocalDate.now().plusMonths(4)
        val credit: CreditDto = buildCreditDto(
            customerId = customerId, dayFirstInstallment = invalidDate
        )
        val creditAsString = objectMapper.writeValueAsString(credit)

        //when
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(creditAsString)
        )   //then
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.size()")
                .value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.dayFirstInstallment")
                .value("Date should be present or up to 3 months in the future"))
            .andDo(MockMvcResultHandlers.print())
    }

    fun buildCustomerDto(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "28475934625",
        email: String = "camila@gmail.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua da Cami",
        income: BigDecimal = BigDecimal.valueOf(1000.0)

        ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        zipcode = zipCode,
        street = street,
        income = income,
    )

//    fun buildCustomer(
//        firstName: String = "Cami",
//        lastName: String = "Cavalcante",
//        cpf: String = "28475934625",
//        email: String = "camila@gmail.com",
//        password: String = "12345",
//        zipCode: String = "12345",
//        street: String = "Rua da Cami",
//        income: BigDecimal = BigDecimal.valueOf(1000.0),
//        id: Long = 1
//
//    ) = Customer(
//        firstName = firstName,
//        lastName = lastName,
//        cpf = cpf,
//        email = email,
//        password = password,
//        address = Address(
//        zipcode = zipCode,
//        street = street
//        ),
//        income = income,
//        id = id
//    )

    fun buildCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(Random.nextDouble(50.00,10000.00)),
        dayFirstInstallment: LocalDate = ThisSysTestUtils.dateWithinRange(LocalDate.now(),3),
        numberOfInstallments: Int = Random.nextInt(1,48),
        customerId: Long
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )
}