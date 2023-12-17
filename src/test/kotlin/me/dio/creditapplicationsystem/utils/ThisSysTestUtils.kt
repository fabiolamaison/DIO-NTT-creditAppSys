package me.dio.creditapplicationsystem.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue
import kotlin.random.Random

class ThisSysTestUtils {

    companion object {
        fun idOtherThan(validId: Long): Long {
            var invalidId : Long
            do {
                invalidId = Random.nextLong().absoluteValue

            } while(validId == invalidId)
            return invalidId
        }

        fun uuidOtherThan(validUUID: UUID): UUID {
            var invalidUUID : UUID
            do {
                invalidUUID = UUID.randomUUID()

            } while(validUUID == invalidUUID)
            return invalidUUID
        }


        fun dateWithinRange(startDate: LocalDate, monthRange: Long): LocalDate {
            val endDate = startDate.plusMonths(monthRange)
            val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)
            val randomDays = ThreadLocalRandom.current().nextLong(daysBetween)
            return startDate.plusDays(randomDays)
        }
    }

}