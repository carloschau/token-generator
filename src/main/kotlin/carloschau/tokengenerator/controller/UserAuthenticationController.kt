package carloschau.tokengenerator.controller

import carloschau.tokengenerator.model.dao.user.UserStatus
import carloschau.tokengenerator.response.model.user.LoginDto
import carloschau.tokengenerator.response.model.user.LoginStatus
import carloschau.tokengenerator.service.AuthenticationService
import carloschau.tokengenerator.service.UserService
import carloschau.tokengenerator.util.JwtTokenUtil
import io.jsonwebtoken.Claims
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class UserAuthenticationController {

    @Autowired
    private lateinit var userService : UserService

    @Autowired
    private lateinit var authenticationService : AuthenticationService

    @Autowired
    private  lateinit var jwtTokenUtil : JwtTokenUtil

    @PostMapping("/login")
    fun login(@RequestBody @Valid request : LoginRequest, @RequestHeader("user-agent") userAgent : String) : ResponseEntity<LoginDto>
    {
        val user = userService.authenticate(request.email, request.password)
        return user?.let {
            val loginStatus =
                    when (user.status) {
                        UserStatus.INACTIVE -> LoginStatus.INACTIVE
                        UserStatus.LOCKED -> LoginStatus.ACCOUNT_LOCKED
                        else -> LoginStatus.SUCCESS
                    }

            val tokenString = when (loginStatus){
                LoginStatus.SUCCESS -> {
                    val authenticationToken = authenticationService.issueAuthenticationToken(it, userAgent)
                    val claims = mapOf<String, Any>(
                            Claims.SUBJECT to user.username,
                            Claims.ID to authenticationToken.accessToken,
                            Claims.EXPIRATION to authenticationToken.expiration,
                            Claims.ISSUED_AT to authenticationToken.expiration
                    )
                    jwtTokenUtil.getJwt(claims)
                }
                else -> null
            }

            ResponseEntity(LoginDto(loginStatus, tokenString), HttpStatus.OK)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping("/register")
    fun createUser(@RequestBody @Valid request: CreateUserRequest)
    {
        userService.createUser(request.username, request.email, request.password)
    }
}