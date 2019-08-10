package carloschau.tokengenerator.controller

import carloschau.tokengenerator.dto.model.token.TokenDto
import carloschau.tokengenerator.dto.model.token.TokenGroupDto
import carloschau.tokengenerator.model.token.TokenGroup
import carloschau.tokengenerator.service.TokenGenerationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class ApiController {
    val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGenerationService : TokenGenerationService

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
}