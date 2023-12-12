package me.dio.creditapplicationsystem.util

import java.time.LocalDate

class DateValidationUtils {
    companion object {
        fun isMaxThreeMonths(value: LocalDate): Boolean {
            if(value == null) {
                return false
            } else {
            val currentDate = LocalDate.now()
            val maxAllowedDate = currentDate.plusMonths(3)

            return value.isEqual(currentDate) || (value.isAfter(currentDate) && value.isBefore(maxAllowedDate))
            }
        }
    }
}