package carloschau.tokengenerator.exception

import org.springframework.http.HttpStatus

open class GeneralApiException(message : String?, val status : HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR) : Exception(message)
class UnauthorizedException(message : String?) : GeneralApiException(message, HttpStatus.UNAUTHORIZED)
class AuthenticationErrorException(message : String?) : GeneralApiException(message, HttpStatus.BAD_REQUEST)