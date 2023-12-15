package me.dio.creditapplicationsystem.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.exception.BusinessException
import me.dio.creditapplicationsystem.repository.CustomerRepository
import me.dio.creditapplicationsystem.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*
import kotlin.math.absoluteValue


@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var customerService: CustomerService
    @Test
    fun `shouldCreateCustomer` (){
        //given
        val fakeCustomer: Customer = buildCustomer()
        every { customerRepository.save(any()) } returns fakeCustomer
        //when
        val actual: Customer = customerService.save(fakeCustomer)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.save(fakeCustomer) }
    }

    @Test
    fun `should find customer by id`(){
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns (Optional.of(fakeCustomer))
        //when
        val actualCustomer: Customer = customerService.findById(fakeId)
        //then
        Assertions.assertThat(actualCustomer).isNotNull
        Assertions.assertThat(actualCustomer).isExactlyInstanceOf(Customer::class.java)
        Assertions.assertThat(actualCustomer).isSameAs(fakeCustomer)
        verify (exactly = 1) { customerRepository.findById(fakeId) }
    }
    @Test
    fun `should not find customer by id and throw BusinessException`(){
        //given
        val fakeId: Long = Random().nextLong().absoluteValue
        every { customerRepository.findById(fakeId) } returns (Optional.empty())
        //when

        //then
        Assertions.assertThatExceptionOfType(EntityNotFoundException::class.java)
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("Id $fakeId not found")
        verify(exactly = 1){customerRepository.findById(fakeId)}
    }

    @Test
    fun `should delete costumer by Id`(){
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns (Optional.of(fakeCustomer))
        every { customerRepository.delete(fakeCustomer) }.just(runs)
        //when
        customerService.delete(fakeId)
        //then
        verify(exactly = 1){customerRepository.findById(fakeId)}
        verify(exactly = 1){customerRepository.delete(fakeCustomer)}
    }
    fun buildCustomer(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "28475934625",
        email: String = "camila@gmail.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua da Cami",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        id: Long = 1L
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipcode = zipCode,
            street = street,
        ),
        income = income,
        id = id
    )
}

