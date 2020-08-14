package carloschau.tokengenerator.controller

import carloschau.tokengenerator.constant.token.TokenParameter
import carloschau.tokengenerator.model.dto.request.token.CreateToken
import carloschau.tokengenerator.model.dto.request.token.CreateTokenGroup
import carloschau.tokengenerator.model.dto.request.token.RevokeTokenRequest
import carloschau.tokengenerator.model.dto.request.token.VerifyTokenRequest
import carloschau.tokengenerator.model.dto.response.token.TokenGroupDto
import carloschau.tokengenerator.model.dto.response.token.VerifyTokenDto
import carloschau.tokengenerator.model.token.TokenType
import carloschau.tokengenerator.model.token.TokenVerifyResult
import carloschau.tokengenerator.security.AuthenticationDetails
import carloschau.tokengenerator.service.token.TokenGenerationService
import carloschau.tokengenerator.service.token.TokenGroupService
import carloschau.tokengenerator.util.QRCodeUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import javax.validation.Valid

@RestController
@RequestMapping("/token")
class TokenController {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGenerationService : TokenGenerationService

    @Autowired
    private lateinit var tokenGroupService : TokenGroupService

    @GetMapping("/{uuid}")
    fun token(
            @PathVariable uuid: String
    ): ResponseEntity<Any> {
        val token = tokenGenerationService.getTokenByUuid(uuid) ?: return ResponseEntity.notFound().build()

        val type: String? = token.meta[TokenParameter.TYPE]
        val media: String? = token.media
        val size: Int? = token.meta[TokenParameter.SIZE]?.toInt()
        return type.let{
            TokenType.fromValue(type ?: TokenType.QR_CODE.value) ?:
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Value $type for parameter [${TokenParameter.TYPE}] is incorrect")
        }.let { tokenType ->
            val jwt = tokenGenerationService.tokenToJwt(token)
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

    @PostMapping
    //TODO: add API key for domain restriction
    fun createToken(@RequestBody @Valid request : CreateToken,
                    @RequestParam requestParam: Map<String, String>,
                    @RequestHeader("Idempotent-Key") idempotentKey: String? ): RedirectView{
        logger.info("Generating token.... uuid:${request.tokenGroupUuid}, type: ${request.type}, media: ${request.media}")
        val tokenType = request.type.let{
            TokenType.fromValue(it ?: TokenType.QR_CODE.value) ?:
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Value $it for parameter [${TokenParameter.TYPE}] is incorrect")
        }
        val token = tokenGenerationService.generateToken(request.tokenGroupUuid, tokenType, request.media, requestParam)
        return RedirectView("token/${token.uuid}")
    }

    @PostMapping("/tokengroup")
    fun createTokenGroup(@RequestBody @Valid request : CreateTokenGroup): String
    {
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails
        return tokenGroupService.createTokenGroup(request, authenticationDetails.userId)
    }

    @GetMapping("/tokengroup")
    fun getAllTokenGroups() : List<TokenGroupDto>
    {
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails
        return tokenGroupService.findTokenGroupsByOwner(authenticationDetails.userId)
                .map { tokenGroup -> TokenGroupDto(tokenGroup) }
    }

    @PostMapping("/verify")
    fun verifyToken(@RequestBody @Valid request : VerifyTokenRequest) : ResponseEntity<VerifyTokenDto>
    {
        val tokenVerifyResult = tokenGenerationService.verifyToken(request.token)
        val responseStatus = when (tokenVerifyResult){
            TokenVerifyResult.SUCCESS -> HttpStatus.OK
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        return ResponseEntity(VerifyTokenDto(tokenVerifyResult.reason), responseStatus)
    }

    @DeleteMapping
    fun revokeToken(@RequestBody @Valid request : RevokeTokenRequest): ResponseEntity<Any>{
        return if (tokenGenerationService.revokeToken(request.uuid))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }
}