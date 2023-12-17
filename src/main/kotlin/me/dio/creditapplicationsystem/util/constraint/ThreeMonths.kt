package me.dio.creditapplicationsystem.util.constraint

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import me.dio.creditapplicationsystem.exception.BusinessException
import me.dio.creditapplicationsystem.util.DateValidationUtils
import java.time.LocalDate
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [MaxThreeMonthsValidator::class])
annotation class MaxThreeMonths(
    val message: String = "Date should be present or up to 3 months in the future",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)

class MaxThreeMonthsValidator : ConstraintValidator<MaxThreeMonths, LocalDate> {
    override fun isValid(value: LocalDate?, context: ConstraintValidatorContext?): Boolean {

        return value?.let { DateValidationUtils.isMaxThreeMonths(it) } ?:
        throw BusinessException("First Installment need to be within 3 months from ${LocalDate.now()}")
    }
}