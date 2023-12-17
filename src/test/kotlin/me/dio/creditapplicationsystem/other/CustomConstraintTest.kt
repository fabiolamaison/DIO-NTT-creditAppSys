package me.dio.creditapplicationsystem.other

import me.dio.creditapplicationsystem.util.DateValidationUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

class CustomConstraintTest() {

    val dateValidation: DateValidationUtils = DateValidationUtils()

    @Test
    fun `Should validate date due to it being between the present and 3 months ahead`() {
        //given
        val validDate: LocalDate = LocalDate.now().plusMonths(2)
        //when
        //then
        Assertions.assertTrue(dateValidation.isMaxThreeMonths(validDate))
    }

    @Test
    fun `Should NOT validate date due to it being more than 3 months ahead`() {
        //given
        val invalidDate: LocalDate = LocalDate.now().plusMonths(4)
        //when
        //then
        Assertions.assertFalse(dateValidation.isMaxThreeMonths(invalidDate))
    }

    @Test
    fun `Should NOT validate date due to it being in the past`() {
        //given
        val invalidPastDate: LocalDate = LocalDate.now().minusMonths(2)
        //when
        //then
        Assertions.assertFalse(dateValidation.isMaxThreeMonths(invalidPastDate))
    }

}
