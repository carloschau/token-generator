package carloschau.tokengenerator.controller

import carloschau.tokengenerator.exception.GeneralApiException
import carloschau.tokengenerator.model.error.GeneralError
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.RuntimeException

@ControllerAdvice
public class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [ GeneralApiException::class ])
    fun handleGeneralApiException(ex: GeneralApiException, request: WebRequest) : ResponseEntity<Any>?
    {
        var responseBody = GeneralError(ex.status.value(), ex.message)
        return handleExceptionInternal(ex, responseBody, HttpHeaders.EMPTY, ex.status, request)
    }
}

data class ErrorResponseBody(val message: String)