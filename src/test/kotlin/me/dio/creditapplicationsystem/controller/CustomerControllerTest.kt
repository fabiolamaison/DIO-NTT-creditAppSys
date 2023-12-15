package me.dio.creditapplicationsystem.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.creditapplicationsystem.dto.CustomerDto
import me.dio.creditapplicationsystem.dto.CustomerUpdateDto
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.repository.CustomerRepository
import me.dio.creditapplicationsystem.utils.ThisSysUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.isEqualTo
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerControllerTest {
    @Autowired private lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach fun setup() = customerRepository.deleteAll()

    @AfterEach fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer returning 201 status`() {
        //given
        val customerDto: CustomerDto = buildCustomerDto()
        val customerAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON).content(customerAsString))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Cami"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Cavalcante"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("camila@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipcode").value("12345"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua da Cami"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(BigDecimal.valueOf(1000.0)))
            .andExpect(MockMvcResultMatchers.status().isEqualTo(201))
            .andDo(MockMvcResultHandlers.print())
        //then
    }

    @Test
    fun `should NOT save customer with same cpf returning 409 status`(){
        //given
        val customerDto: CustomerDto = buildCustomerDto()
        val dtoAsString: String = objectMapper.writeValueAsString(customerDto)
        customerRepository.save(customerDto.toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON).content(dtoAsString))
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.currentTime").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }


    @Test
    fun `should NOT save a customer with empty firstName returning 400 status`() {
        //given
        val customerDto: CustomerDto = buildCustomerDto(firstName = "")
        val dtoAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .content(dtoAsString)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.currentTime").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id returning status 200`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Cami"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Cavalcante"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("camila@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipcode").value("12345"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua da Cami"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(BigDecimal.valueOf(1000.0)))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should NOT find customer by invalid id returning status 404`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val invalidId: Long = ThisSysUtils.idOtherThan(customer.id!!)
        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/$invalidId")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.currentTime").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.creditapplicationsystem.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete user by id and return no content`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should NOT delete user by id and return status 400`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val invalidId: Long = ThisSysUtils.idOtherThan(customer.id!!)
        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${invalidId}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should update user and return status 200`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerUpdateDto : CustomerUpdateDto = buildCustomerUpdateDto()
        val customerUpdateString: String = objectMapper.writeValueAsString(customerUpdateDto)
        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(customerUpdateString))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Hadouken"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Honduras"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(BigDecimal.valueOf(5000.0)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipcode").value("1234567890"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Route 63"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("camila@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should NOT update user and return status 400`(){
        //given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerUpdateDto : CustomerUpdateDto = buildCustomerUpdateDto()
        val customerUpdateString: String = objectMapper.writeValueAsString(customerUpdateDto)
        val invalidId: Long = ThisSysUtils.idOtherThan(customer.id!!)
        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${invalidId}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(customerUpdateString))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
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
        income: BigDecimal = BigDecimal.valueOf(1000.0),

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

    fun buildCustomerUpdateDto(
        firstName: String = "Hadouken",
        lastName: String = "Honduras",
        income: BigDecimal = BigDecimal.valueOf(5000.0),
        zipcode: String = "1234567890",
        street: String = "Route 63"
    ) = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipcode = zipcode,
        street = street
    )

}