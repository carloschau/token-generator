package carloschau.tokengenerator.controller

import carloschau.tokengenerator.response.model.token.TokenDto
import carloschau.tokengenerator.response.model.token.TokenGroupDto
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

    @GetMapping("/token/{uuid}")
    fun token(@PathVariable uuid: String): TokenDto {
        with (tokenGenerationService.getToken(uuid)) {
            return if (this != null) TokenDto(this.jwt) else TokenDto()
        }
    }

    @PostMapping("/tokengroup")
    fun createTokenGroup(@RequestBody @Valid request : CreateTokenGroupRequest)
    {
        tokenGenerationService.createTokenGroup(request.name)
    }

    @GetMapping("/tokengroup")
    fun getAllTokenGroups() : List<TokenGroupDto>
    {
        return tokenGenerationService.findAllTokenGroup().map { tokenGroup -> TokenGroupDto(tokenGroup) }
    }




    @RequestMapping("/hello")
    fun hello(){

    }
}