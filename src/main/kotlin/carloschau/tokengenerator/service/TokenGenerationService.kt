package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.token.TokenGroup
import carloschau.tokengenerator.model.token.Token
import carloschau.tokengenerator.repository.token.TokenGroupRepository
import carloschau.tokengenerator.repository.token.TokenRepository
import carloschau.tokengenerator.security.AuthenticationDetails
import carloschau.tokengenerator.util.UuidUtil
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.bson.types.Binary
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenGenerationService{
    val logger: Logger? = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGroupRepository: TokenGroupRepository

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    fun getToken(uuidStr: String): Token? {
        val uuid = UuidUtil.fromStringWithoutDash(uuidStr);
        val tokenGroup = tokenGroupRepository.findByUuid(Binary( UuidUtil.toBytes(uuid)))
        logger?.debug("Token Group with uuid {} is {}".format(uuid,  tokenGroup?.id?:"Not Found"))

        return tokenGroup?.run group@ {
            val jwtString = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("alg", "HS256")
                    .setIssuedAt(Date())
                    .signWith(this@group.signingKey)
                    .compact()

            val token = Token().apply {
                this.jwt = jwtString
                this.tokenGroup_Id = this@group.id
            }

            tokenRepository.save(token)
            token
        }
    }

    fun createTokenGroup(name : String){
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails

        val tokenGroup = TokenGroup().apply {
            this.name = name
            this.uuid = UUID.randomUUID()
            this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
            this.ownerId = authenticationDetails.userId
        }

        logger?.debug("Token group uuid created: ${ tokenGroup.uuid }")
        tokenGroupRepository.save(tokenGroup)
    }

    fun findAllTokenGroup() : List<TokenGroup>{
        return tokenGroupRepository.findAll().filterNotNull()
    }
}