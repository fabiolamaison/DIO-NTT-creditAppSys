package me.dio.creditapplicationsystem.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import me.dio.creditapplicationsystem.dto.CreditDto
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.exception.BusinessException
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.repository.CustomerRepository
import me.dio.creditapplicationsystem.service.impl.CreditService
import me.dio.creditapplicationsystem.service.impl.CustomerService
import me.dio.creditapplicationsystem.utils.ThisSysTestUtils
import org.assertj.core.api.AssertionInfo
import org.assertj.core.api.Assertions
import org.assertj.core.api.ListAssert
import org.assertj.core.description.Description
import org.hibernate.Length
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.opentest4j.AssertionFailedError
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.collections.List
import kotlin.math.absoluteValue
import kotlin.random.Random


@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var creditRepository: CreditRepository
    @InjectMockKs lateinit var creditService: CreditService
    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var customerService: CustomerService

    @Test
    fun `should save credit`(){
        //given
        val fakeCustomer: Customer = buildCustomer()
        val fakeCredit: Credit = buildCredit(customerId = fakeCustomer.id!!)
        every { creditRepository.save(any()) } returns fakeCredit
        //when
        val actual: Credit = creditRepository.save(fakeCredit)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

//    @Test
//    fun `should not save credit due to invalid customer id`(){
//        //given
//        val fakeCustomer: Customer = buildCustomer()
//        val invalidId: Long = ThisSysTestUtils.idOtherThan(fakeCustomer.id!!)
//        val fakeCredit: Credit = buildCredit(customerId = invalidId)
//        //when
//        every { customerRepository.findById(fakeCredit.customer?.id!!)} returns (Optional.empty())
//        //then
//        Assertions.assertThatThrownBy { creditRepository.save(fakeCredit) }
//            .isInstanceOf(RuntimeException::class.java)
//            .withFailMessage("Invalid customer ID ${fakeCredit.customer?.id!!}")
//        verify(exactly = 1) { creditRepository.save(fakeCredit) }
//    }

    @Test
    fun `should return credit through credit UUID code`(){
        //given
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCustomer: Customer = buildCustomer()
        val fakeCredit: Credit = buildCredit(customerId = fakeCustomer.id!!, creditCode = fakeCreditCode)
        every { creditRepository.findByCreditCode(any()) } returns fakeCredit
        //when
        val actualCredit: Credit? = creditRepository.findByCreditCode(fakeCreditCode)
        //then
        Assertions.assertThat(actualCredit).isNotNull
        Assertions.assertThat(actualCredit).isSameAs(fakeCredit)
        Assertions.assertThat(actualCredit).isExactlyInstanceOf(Credit::class.java)
        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }
    }


    @Test
    fun `should return credit list through customer ID`(){
        //given
        val fakeCustomer: Customer = buildCustomer()
        val fakeCustomerId: Long = fakeCustomer.id!!
        val fakeCreditList: MutableList<Credit> = mutableListOf();
        val desiredListLength: Int = Random.nextInt(0,101)
        do {
            fakeCreditList.add(buildCredit(customerId = fakeCustomerId))
        } while (fakeCreditList.size<desiredListLength)
        every { creditRepository.findAllByCustomerId(any()) } returns fakeCreditList
        //when
        val customerCreditList: List<Credit> = creditRepository.findAllByCustomerId(fakeCustomerId)
        //then
        Assertions.assertThat(customerCreditList).isNotNull
        Assertions.assertThat(customerCreditList).isSameAs(fakeCreditList)
        Assertions.assertThat(customerCreditList).isExactlyInstanceOf(ArrayList::class.java)
        Assertions.assertThat(customerCreditList).allMatch {it is Credit}
        Assertions.assertThat(customerCreditList.size).isEqualTo(desiredListLength)
        verify(exactly = 1) { creditRepository.findAllByCustomerId(fakeCustomerId) }
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

    fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.valueOf(Random.nextDouble(50.00,10000.00)),
        dayFirstInstallment: LocalDate = ThisSysTestUtils.dateWithinRange(LocalDate.now(),3),
        numberOfInstallments: Int = Random.nextInt(1,48),
        customerId: Long
    ) = Credit(
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        id = customerId
    )

}
