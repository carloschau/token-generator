package carloschau.tokengenerator.service.token

import carloschau.tokengenerator.constant.token.TokenPatternPlaceholder
import carloschau.tokengenerator.model.dao.token.Token
import carloschau.tokengenerator.model.dao.token.TokenGroup
import carloschau.tokengenerator.model.token.TokenType
import carloschau.tokengenerator.model.token.TokenVerifyResult
import carloschau.tokengenerator.repository.token.TokenGroupRepository
import carloschau.tokengenerator.repository.token.TokenRepository
import carloschau.tokengenerator.util.UuidUtil
import io.jsonwebtoken.*
import io.jsonwebtoken.security.SignatureException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.crypto.SecretKey

@Service
class TokenGenerationService{
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGroupRepository: TokenGroupRepository

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    fun generateToken(uuidStr: String, type: TokenType, media: String?, paramSource: Map<String, String>): String {
        val uuid = UuidUtil.fromStringWithoutDash(uuidStr);
        val tokenGroup = tokenGroupRepository.findByUuid(uuid)
        logger.debug("Token Group with uuid $uuid is ${tokenGroup?.id?:"Not Found"}")

        return tokenGroup?.let { group ->
            if (!group.canIssueToken)
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        group.tokenGroupStatus.reason)
            val token = Token().apply {
                this.uuid = UUID.randomUUID()
                this.media = media
                this.groupId = tokenGroup.id
                this.type = type
                this.issueAt = Date()
                if (group.tokenLifetime > 0)
                    this.expireAt = Date(issueAt.time + group.tokenLifetime * 1000)
            }

            val resultToken = generateJwtByToken(token, group.signingKey!!).let {jwt ->
                if (!group.pattern.isNullOrBlank())
                    massageWithPattern(group, jwt, paramSource)
                else
                    jwt
            }

            tokenRepository.save(token)
            tokenGroupRepository.incNumberOfTokenIssued(group.id!!)
            resultToken
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Token group not found with uuid")
    }

    fun generateJwtByToken(token: Token, signingKey: SecretKey): String{
        return Jwts.builder()
                .setHeaderParam(JwsHeader.TYPE, JwsHeader.JWT_TYPE)
                .setHeaderParam(JwsHeader.KEY_ID, token.groupId)
                .setIssuedAt(token.issueAt)
                .setId(token.uuid.toString())
                .run {
                    if (token.expireAt != null)
                        setExpiration(token.expireAt)
                    else
                        this
                }
                .signWith(signingKey)
                .compact()
    }

    private fun massageWithPattern(tokenGroup: TokenGroup, token: String, paramSource: Map<String, String>): String{
        val patternParams = tokenGroup.patternParameters
        if (!patternParams.contains(TokenPatternPlaceholder.EMPTY) && !patternParams.contains(TokenPatternPlaceholder.TOKEN))
        {
            "Invalid token pattern! token placeholder({} or {token}) not found".also(logger::error).also {
                throw Exception(it)
            }
        }

        var result = tokenGroup.pattern!!
        patternParams.forEach {
            result = when(it){
                TokenPatternPlaceholder.EMPTY -> {
                    if (patternParams.size > 1) {
                        "Invalid token pattern! {} found while more than 1 parameter in pattern".also(logger::error).also{ msg ->
                            throw Exception(msg)
                        }
                    }
                    result.replace("{$it}", token)
                }
                TokenPatternPlaceholder.TOKEN ->
                    result.replace("{$it}", token)
                else ->
                    result.replace("{$it}",paramSource.getValue(it))
            }
        }
        return result
    }

    fun verifyToken(tokenStr : String): TokenVerifyResult{
        return try {
            getJwsFromString(tokenStr).let {
                val uuidStr = it.body.id
                val token = tokenRepository.findByUuid(UUID.fromString(uuidStr))
                when{
                    token == null -> TokenVerifyResult.NOT_FOUND
                    !token.isActive -> TokenVerifyResult.REVOKED
                    else -> TokenVerifyResult.SUCCESS
                }
            }
        }
        catch (signEx: SignatureException){
            signEx.toString().also(logger::error)
            TokenVerifyResult.SIGNATURE_INVALID
        }
        catch (expiredEx: ExpiredJwtException){
            expiredEx.toString().also(logger::error)
            TokenVerifyResult.EXPIRED
        }
        catch (e: Exception){
            e.toString().also(logger::error)
            TokenVerifyResult.UNKNOWN
        }
    }

    fun getJwsFromString(jwtStr: String) : Jws<Claims> {
        return Jwts.parserBuilder()
                .setSigningKeyResolver(tokenGroupSigningKeyResolverWrapper())
                .build()
                .parseClaimsJws(jwtStr)
    }

    fun tokenGroupSigningKeyResolverWrapper(): TokenGroupSigningKeyResolver{
        return TokenGroupSigningKeyResolver(tokenGroupRepository)
    }
}