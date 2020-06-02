package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.token.TokenGroup
import carloschau.tokengenerator.model.dto.request.token.CreateTokenGroup
import carloschau.tokengenerator.model.dao.token.Token
import carloschau.tokengenerator.model.token.TokenType
import carloschau.tokengenerator.repository.token.TokenGroupRepository
import carloschau.tokengenerator.repository.token.TokenRepository
import carloschau.tokengenerator.util.UuidUtil
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.crypto.SecretKey

@Service
class TokenGenerationService{
    val logger: Logger? = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGroupRepository: TokenGroupRepository

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    fun generateToken(uuidStr: String, type: TokenType, media: String?): String {
        val uuid = UuidUtil.fromStringWithoutDash(uuidStr);
        val tokenGroup = tokenGroupRepository.findByUuid(uuid)
        logger?.debug("Token Group with uuid $uuid is ${tokenGroup?.id?:"Not Found"}")

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
            val jwt = generateJwtByToken(token, group.signingKey!!)
            tokenRepository.save(token)
            jwt
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Token group not found with uuid")
    }

    fun generateJwtByToken(token: Token, signingKey: SecretKey): String{
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(Date())
                .setId(token.uuid.toString())
                .signWith(signingKey)
                .compact()
    }

    fun createTokenGroup(createTokenGroup : CreateTokenGroup, userId: String): String{
        val tokenGroup = TokenGroup().apply {
            this.name = createTokenGroup.name
            this.uuid = UUID.randomUUID()
            this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
            this.ownerId = userId
            this.effectiveFrom = createTokenGroup.effectiveFrom
            this.effectiveTo = createTokenGroup.effectiveTo
            this.maxTokenIssuance = createTokenGroup.maxTokenIssuance
        }

        logger?.debug("Token group uuid created: ${ tokenGroup.uuid }")
        tokenGroupRepository.save(tokenGroup)
        return tokenGroup.uuid.toString()
    }

    fun findTokenGroupsByOwner(userId: String) : List<TokenGroup>{
        return tokenGroupRepository.findByOwnerId(userId)
    }

    fun findAllTokenGroup() : List<TokenGroup>{
        return tokenGroupRepository.findAll().filterNotNull()
    }
}