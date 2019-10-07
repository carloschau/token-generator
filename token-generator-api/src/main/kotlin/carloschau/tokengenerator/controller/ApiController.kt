package carloschau.tokengenerator.controller

import carloschau.tokengenerator.dto.model.token.TokenDto
import carloschau.tokengenerator.dto.model.token.TokenGroupDto
import carloschau.tokengenerator.dto.model.user.LoginStatus
import carloschau.tokengenerator.dto.model.user.LoginStatus.*
import carloschau.tokengenerator.model.user.UserStatus
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
    private  lateinit var userService : UserService

    @GetMapping("/api/token/{uuid}")
    fun token(@PathVariable uuid: String): TokenDto {
        return tokenGenerationService.getToken(uuid)
    }

    @PostMapping("/api/tokengroup")
    fun createTokenGroup(@RequestBody @Valid request : CreateTokenGroupRequest)
    {
        tokenGenerationService.createTokenGroup(request.name)
    }

    @GetMapping("/api/tokengroup")
    fun getAllTokenGroups() : List<TokenGroupDto>
    {
        return tokenGenerationService.findAllTokenGroup()
    }

    @PostMapping("/api/register")
    fun createUser(@RequestBody @Valid request: CreateUserRequest)
    {
        userService.createUser(request.username, request.email, request.password)
    }

    @PostMapping("/api/login")
    fun login(@RequestBody @Valid request : LoginRequest)
    {
        val user = userService.authenticate(request.email, request.password)
        val loginStatus =
                if (user == null) EMAIL_OR_PASSWORD_ERROR
                else if (user.status == UserStatus.INACTIVE) INACTIVE
                else if (user.status == UserStatus.LOCKED) ACCOUNT_LOCKED
                else SUCCESS



    }
}