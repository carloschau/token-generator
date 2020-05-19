package carloschau.tokengenerator.controller

import carloschau.tokengenerator.model.user.UserStatus
import carloschau.tokengenerator.response.model.user.LoginDto
import carloschau.tokengenerator.response.model.user.LoginStatus
import carloschau.tokengenerator.service.AuthenticationService
import carloschau.tokengenerator.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class UserAuthenticationController {

    @Autowired
    private lateinit var userService : UserService

    @Autowired
    private lateinit var authenticationService : AuthenticationService

    @PostMapping("/login")
    fun login(@RequestBody @Valid request : LoginRequest, @RequestHeader("user-agent") userAgent : String) : ResponseEntity<LoginDto>
    {
        val user = userService.authenticate(request.email, request.password)
        return user?.let {
            val loginStatus =
                    when {
                        user == null -> LoginStatus.EMAIL_OR_PASSWORD_ERROR
                        user.status == UserStatus.INACTIVE -> LoginStatus.INACTIVE
                        user.status == UserStatus.LOCKED -> LoginStatus.ACCOUNT_LOCKED
                        else -> LoginStatus.SUCCESS
                    }

            val tokenString = when (loginStatus){
                LoginStatus.SUCCESS -> {
                    val authenticationToken = authenticationService.issueAuthenticationToken(it, userAgent)
                    userService.addAccessTokenToUser(it, authenticationToken.accessToken)
                    authenticationToken.token
                }
                else -> null
            }

            ResponseEntity(LoginDto(loginStatus, tokenString), HttpStatus.OK)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }
}