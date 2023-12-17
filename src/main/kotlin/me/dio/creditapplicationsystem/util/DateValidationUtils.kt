package me.dio.creditapplicationsystem.util

import me.dio.creditapplicationsystem.exception.BusinessException
import java.time.LocalDate

class DateValidationUtils {
    companion object {
        fun isMaxThreeMonths(value: LocalDate): Boolean {
            val currentDate = LocalDate.now()
            val maxAllowedDate = currentDate.plusMonths(3)

            if (value.isAfter(currentDate.minusDays(1)) && value.isBefore(maxAllowedDate.plusDays(1))) {
                return true
            } else {
                return false
            }
        }
    }
}