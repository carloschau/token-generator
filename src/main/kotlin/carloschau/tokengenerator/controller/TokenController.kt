package carloschau.tokengenerator.controller

import carloschau.tokengenerator.model.dto.request.token.CreateTokenGroup
import carloschau.tokengenerator.model.dto.response.token.TokenGroupDto
import carloschau.tokengenerator.model.token.TokenType
import carloschau.tokengenerator.security.AuthenticationDetails
import carloschau.tokengenerator.service.TokenGenerationService
import carloschau.tokengenerator.service.UserService
import carloschau.tokengenerator.util.QRCodeUtil
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
class TokenController {
    val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGenerationService : TokenGenerationService

    @Autowired
    private lateinit var userService : UserService

    @GetMapping("/token/{uuid}")
    fun token(
            @PathVariable uuid: String,
            @RequestParam requestParam: Map<String, String>
    ): ResponseEntity<Any> {
        val type: String? = requestParam["typ"]
        val media: String? = requestParam["media"]
        val size: Int? = requestParam["size"]?.toInt()
        return type.let{
            TokenType.fromValue(type ?: TokenType.QR_CODE.value) ?:
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Value $type for parameter [typ] is incorrect")
        }.let { tokenType ->
            logger.info("Generating token.... uuid:$uuid, type: ${type}, media: $media")

            val jwt = tokenGenerationService.generateToken(uuid, tokenType, media, requestParam)
            when (tokenType){
                TokenType.QR_CODE ->
                    ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                            .body(QRCodeUtil.generateQRCode(jwt, size))
                TokenType.TEXT ->
                    ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                        .body(jwt)
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