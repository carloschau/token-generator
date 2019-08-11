package carloschau.tokengenerator.service

import carloschau.tokengenerator.dto.model.token.TokenDto
import carloschau.tokengenerator.dto.model.token.TokenGroupDto
import carloschau.tokengenerator.model.token.Token
import carloschau.tokengenerator.model.token.TokenGroup
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

    fun getToken(uuidStr: String): TokenDto {
        val uuid = UuidUtil.fromStringWithoutDash(uuidStr);
        val bsonUUID = UuidUtil.toStandardBinaryUUID(uuid);
        val tokenGroup = TokenGroup.fromDao(tokenGroupRepository.findByUuid(Binary( UuidUtil.toBytes(uuid))))
        logger?.info(tokenGroup?.Id?:"Not Found")

        if (tokenGroup != null)
        {
            val jwtString = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("alg", "HS256")
                    .setIssuedAt(Date())
                    .signWith(tokenGroup.signingKey)
                    .compact()

            val token = Token().apply {
                this.jwt = jwtString
                this.tokenGroup_Id = tokenGroup.Id
            }

            tokenRepository.save(token)
            return token.toDto
        }
        else
            return TokenDto()
    }

    fun createTokenGroup(name : String){
        val tokenGroup = TokenGroup().apply {
            this.name = name
            this.uuid = UUID.randomUUID()
            this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        }

        logger?.info("Token group uuid created: ${ tokenGroup.uuid }")
        tokenGroupRepository.save(tokenGroup.toDao)
    }

    fun findAllTokenGroup() : List<TokenGroupDto>{
        return tokenGroupRepository.findAll().filterNotNull().map { TokenGroup.fromDao(it)!!.toDto }
    }
}