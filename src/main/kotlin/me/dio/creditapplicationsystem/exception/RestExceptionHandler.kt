package me.dio.creditapplicationsystem.exception

import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validExceptionHandler(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionDetails>{
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.stream().forEach() {
            erro: ObjectError ->
            val fieldName : String = (erro as FieldError).field
            val messageError: String? = erro.defaultMessage
            errors[fieldName] = messageError
        }
        return ResponseEntity(
            ExceptionDetails(
                title = "Bad Request! consult the documentation",
                currentTime = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = ex.javaClass.toString(),
                details = errors
            ), HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(DataAccessException::class)
    fun validExceptionHandler(ex: DataAccessException): ResponseEntity<ExceptionDetails>{
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ExceptionDetails(
                    title = "Confilct! consult the documentation",
                    currentTime = LocalDateTime.now(),
                    status = HttpStatus.CONFLICT.value(),
                    exception = ex.javaClass.toString(),
                    details = mutableMapOf(ex.cause.toString() to ex.message)
                )
        )
    }

    @ExceptionHandler(BusinessException::class)
    fun validExceptionHandler(ex: BusinessException): ResponseEntity<ExceptionDetails>{
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionDetails(
                title = "Bad Request! consult the documentation",
                currentTime = LocalDateTime.now(),
                status = HttpStatus.CONFLICT.value(),
                exception = ex.javaClass.toString(),
                details = mutableMapOf(ex.cause.toString() to ex.message)
            )
        )
    }
    @ExceptionHandler(IllegalArgumentException::class)
    fun validExceptionHandler(ex: IllegalArgumentException): ResponseEntity<ExceptionDetails>{
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionDetails(
            title = "Bad Request! consult the documentation",
            currentTime = LocalDateTime.now(),
            status = HttpStatus.CONFLICT.value(),
            exception = ex.javaClass.toString(),
            details = mutableMapOf(ex.cause.toString() to ex.message)
        )
        )
    }

}