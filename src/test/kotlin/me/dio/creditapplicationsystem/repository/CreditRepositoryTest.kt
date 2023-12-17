package me.dio.creditapplicationsystem.repository

import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.utils.ThisSysTestUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {
    @Autowired lateinit var creditRepository: CreditRepository
    @Autowired lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var credit1: Credit
    private lateinit var credit2: Credit

    @BeforeEach fun setup(){
        customer = testEntityManager.merge(buildCustomer())
        credit1 = testEntityManager.persist(buildCredit(customer = customer))
        credit2 = testEntityManager.persist(buildCredit(customer = customer))
    }



    @Test
    fun `should find credit by credit code`(){
        //arrange
        val creditCode1: UUID = UUID.fromString("ddad9f62-cc29-4144-b623-ece7ff140269")
        val creditCode2: UUID = UUID.fromString("a51e4be1-5b57-4d2b-a1f8-e721c5a0c6e4")
        credit1.creditCode = creditCode1
        credit2.creditCode = creditCode2
        //act
        val fakeCredit1: Credit = creditRepository.findByCreditCode(creditCode = creditCode1)!!
        val fakeCredit2: Credit = creditRepository.findByCreditCode(creditCode = creditCode2)!!
        //assert
        Assertions.assertThat(fakeCredit1).isNotNull
        Assertions.assertThat(fakeCredit2).isNotNull
        Assertions.assertThat(fakeCredit1.equals(credit1))
        Assertions.assertThat(fakeCredit2.equals(credit2))
    }

    @Test
    fun `should NOT find credit by credit code and return status 404`(){
        //arrange
        val invalidUUID = ThisSysTestUtils.uuidOtherThan(credit1.creditCode)
        //act
        val fakeCredit1: Credit? = creditRepository.findByCreditCode(creditCode = invalidUUID)
        //assert
        Assertions.assertThat(fakeCredit1).isNull()
    }

    @Test
    fun `should find all credit by customer id`(){
        //arrange
        val customerId: Long = customer.id!!
        //act
        val fakeCreditList: List<Credit> = creditRepository.findAllByCustomerId(customerId)
        //asset
        Assertions.assertThat(fakeCreditList).isNotNull
        Assertions.assertThat(fakeCreditList.size).isEqualTo(2)
        Assertions.assertThat(fakeCreditList).contains(credit1,credit2)
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
        creditValue: BigDecimal = BigDecimal.valueOf(700.0),
        dayFirstInstallment: LocalDate = LocalDate.of(2023,12,30),
        numberOfInstallments: Int = 5,
        customer: Customer,
    ) = Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customer = customer
    )
}