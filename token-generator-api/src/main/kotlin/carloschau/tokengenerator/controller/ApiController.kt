package carloschau.tokengenerator.controller

import carloschau.tokengenerator.response.model.token.TokenDto
import carloschau.tokengenerator.response.model.token.TokenGroupDto
import carloschau.tokengenerator.response.model.user.LoginDto
import carloschau.tokengenerator.response.model.user.LoginStatus.*
import carloschau.tokengenerator.exception.AuthenticationErrorException
import carloschau.tokengenerator.model.user.UserStatus
import carloschau.tokengenerator.service.AuthenticationService
import carloschau.tokengenerator.service.TokenGenerationService
import carloschau.tokengenerator.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class ApiController {
    val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGenerationService : TokenGenerationService

    @Autowired
    private lateinit var userService : UserService

    @Autowired
    private lateinit var authenticationService : AuthenticationService

    @GetMapping("/api/token/{uuid}")
    fun token(@PathVariable uuid: String): TokenDto {
        with (tokenGenerationService.getToken(uuid)) {
            return if (this != null) TokenDto(this.jwt) else TokenDto()
        }
    }

    @PostMapping("/api/tokengroup")
    fun createTokenGroup(@RequestBody @Valid request : CreateTokenGroupRequest)
    {
        tokenGenerationService.createTokenGroup(request.name)
    }

    @GetMapping("/api/tokengroup")
    fun getAllTokenGroups() : List<TokenGroupDto>
    {
        return tokenGenerationService.findAllTokenGroup().map { tokenGroup -> TokenGroupDto(tokenGroup) }
    }

    @PostMapping("/api/register")
    fun createUser(@RequestBody @Valid request: CreateUserRequest)
    {
        userService.createUser(request.username, request.email, request.password)
    }

    @PostMapping("/api/login")
    fun login(@RequestBody @Valid request : LoginRequest, @RequestHeader("user-agent") userAgent : String) : LoginDto
    {
        val user = userService.authenticate(request.email, request.password)
        val loginStatus =
                when {
                    user == null -> EMAIL_OR_PASSWORD_ERROR
                    user.status == UserStatus.INACTIVE -> INACTIVE
                    user.status == UserStatus.LOCKED -> ACCOUNT_LOCKED
                    else -> SUCCESS
                }

        if (loginStatus == SUCCESS && user != null)
        {
            return user.run {
                var authenticationToken = authenticationService.IssueAuthenticationToken(this, userAgent)
                userService.addAccessTokenToUser(this, authenticationToken.accessToken)
                return LoginDto(loginStatus, authenticationToken.token)
            }
        }
        else
            throw AuthenticationErrorException(loginStatus.toString())
    }
}