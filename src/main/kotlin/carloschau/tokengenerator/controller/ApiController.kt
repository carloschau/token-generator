package carloschau.tokengenerator.controller

import carloschau.tokengenerator.dto.model.token.TokenDto
import carloschau.tokengenerator.service.TokenGenerationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController {
    @Autowired
    private lateinit var tokenGenerationService : TokenGenerationService

    @GetMapping("/api/token/{uuid}")
    fun token(@PathVariable uuid: String): TokenDto {

        return tokenGenerationService.getToken(uuid)
    }
}