package carloschau.tokengenerator.controller

import carloschau.tokengenerator.model.dto.request.token.CreateTokenGroup
import carloschau.tokengenerator.model.dto.response.token.TokenDto
import carloschau.tokengenerator.model.dto.response.token.TokenGroupDto
import carloschau.tokengenerator.security.AuthenticationDetails
import carloschau.tokengenerator.service.TokenGenerationService
import carloschau.tokengenerator.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class ApiController {
    val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGenerationService : TokenGenerationService

    @Autowired
    private lateinit var userService : UserService

    @GetMapping("/token/{uuid}")
    fun token(@PathVariable uuid: String): TokenDto {
        with (tokenGenerationService.getToken(uuid)) {
            return if (this != null) TokenDto(this.jwt) else TokenDto()
        }
    }

    @PostMapping("/tokengroup")
    fun createTokenGroup(@RequestBody @Valid request : CreateTokenGroup)
    {
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails
        tokenGenerationService.createTokenGroup(request, authenticationDetails.userId)
    }

    @GetMapping("/tokengroup")
    fun getAllTokenGroups() : List<TokenGroupDto>
    {
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails
        return tokenGenerationService.findTokenGroupsByOwner(authenticationDetails.userId)
                .map { tokenGroup -> TokenGroupDto(tokenGroup) }
    }

    @RequestMapping("/hello")
    fun hello(){

    }
}