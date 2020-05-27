package carloschau.tokengenerator.controller

import carloschau.tokengenerator.model.dto.request.token.CreateTokenGroup
import carloschau.tokengenerator.model.dto.response.token.TokenDto
import carloschau.tokengenerator.model.dto.response.token.TokenGroupDto
import carloschau.tokengenerator.model.token.TokenType
import carloschau.tokengenerator.security.AuthenticationDetails
import carloschau.tokengenerator.service.TokenGenerationService
import carloschau.tokengenerator.service.UserService
import carloschau.tokengenerator.util.QRCodeUtil
import io.jsonwebtoken.Header
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
class ApiController {
    val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGenerationService : TokenGenerationService

    @Autowired
    private lateinit var userService : UserService

    @GetMapping("/token/{uuid}")
    fun token(
            @PathVariable uuid: String,
            @RequestParam("typ") type: String?,
            @RequestParam("media") media: String?
    ): Any {
        return when(type){
            TokenType.QRCode.value, null -> TokenType.QRCode
            TokenType.Text.value -> TokenType.Text
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Token type not found")
        }.let { tokenType ->
            logger.debug("Generating token.... uuid:$uuid, type: ${type}, media: $media")
            val jwt = tokenGenerationService.getToken(uuid, tokenType, media)
            when (tokenType){
                TokenType.QRCode ->
                    ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                            .body(QRCodeUtil.generateQRCode(jwt))
                TokenType.Text -> ResponseEntity.ok(jwt)

            }
        }
    }

    @PostMapping("/tokengroup")
    fun createTokenGroup(@RequestBody @Valid request : CreateTokenGroup): String
    {
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails
        return tokenGenerationService.createTokenGroup(request, authenticationDetails.userId)
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