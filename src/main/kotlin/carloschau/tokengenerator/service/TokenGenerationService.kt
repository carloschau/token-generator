package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.token.TokenGroup
import carloschau.tokengenerator.model.token.Token
import carloschau.tokengenerator.repository.token.TokenGroupRepository
import carloschau.tokengenerator.repository.token.TokenRepository
import carloschau.tokengenerator.util.UuidUtil
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.bson.types.Binary
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
        logger?.info(tokenGroup?.Id?:"Not Found")

        return tokenGroup?.run group@ {
            val jwtString = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("alg", "HS256")
                    .setIssuedAt(Date())
                    .signWith(this@group.signingKey)
                    .compact()

            val token = Token().apply {
                this.jwt = jwtString
                this.tokenGroup_Id = this@group.Id
            }

            tokenRepository.save(token)
            token
        }
    }

    fun createTokenGroup(name : String){
        val ownerId = "5d5010cf2b7a9e3de0b4ff47" //TODO: Get userId of current user

        val tokenGroup = TokenGroup().apply {
            this.name = name
            this.uuid = UUID.randomUUID()
            this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
            this.owner_Id = ownerId
        }

        logger?.info("Token group uuid created: ${ tokenGroup.uuid }")
        tokenGroupRepository.save(tokenGroup)
    }

    fun findAllTokenGroup() : List<TokenGroup>{
        return tokenGroupRepository.findAll().filterNotNull()
    }
}