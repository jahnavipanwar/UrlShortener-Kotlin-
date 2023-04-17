package com.jahnavi.UrlShortener.exception

import com.jahnavi.UrlShortener.bean.ErrorDetails
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import kotlin.jvm.Throws

@ControllerAdvice
class CustomizeResponseEntityHandler {
    @ExceptionHandler(ArgumentNotValidException::class)
    fun notValidArgument(ex:Exception) : ResponseEntity<ErrorDetails>{
        var error=ErrorDetails()
        error.errorDetails="your Given Url is not Valid"
        error.message=ex.message
        return ResponseEntity<ErrorDetails>(error,HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidUserException::class)
    fun notValidUser(ex:Exception) : ResponseEntity<ErrorDetails>{
        var error=ErrorDetails()
        error.errorDetails="your Given User is not Valid"
        error.message=ex.message
        return ResponseEntity<ErrorDetails>(error,HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UrlLengthException::class)
    fun notValidLength(ex:Exception) : ResponseEntity<ErrorDetails>{
        var error=ErrorDetails()
        error.errorDetails="Url too short"
        error.message=ex.message
        println(ex.message);
        return ResponseEntity<ErrorDetails>(error,HttpStatus.NOT_ACCEPTABLE)
    }

    @ExceptionHandler(UrlNotFoundException::class)
    fun notFound(ex:Exception) : ResponseEntity<ErrorDetails>{
        var error=ErrorDetails()
        error.errorDetails="Url not found"
        error.message=ex.message
        println(ex.message);
        return ResponseEntity<ErrorDetails>(error,HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UrlTimeoutException::class)
    fun UrlTimeout(ex:Exception) : ResponseEntity<ErrorDetails>{
        var error=ErrorDetails()
        error.errorDetails="Url no longer exists"
        error.message=ex.message
        println(ex.message);
        return ResponseEntity<ErrorDetails>(error,HttpStatus.GATEWAY_TIMEOUT)
    }
}