package carloschau.tokengenerator.service

import carloschau.tokengenerator.dto.model.token.TokenDto
import carloschau.tokengenerator.model.token.TokenGroup
import carloschau.tokengenerator.repository.token.TokenGroupRepository
import carloschau.tokengenerator.util.UuidUtil
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
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

    fun getToken(uuidStr: String): TokenDto {
        val uuid = UuidUtil.fromStringWithoutDash(uuidStr);
        val bsonUUID = UuidUtil.toStandardBinaryUUID(uuid);
        val tokenGroup = TokenGroup.fromDao(tokenGroupRepository.findByUuid(bsonUUID))
        logger?.info(tokenGroup?.Id?:"Not Found")

        if (tokenGroup != null)
         {
            if (tokenGroup.signingKey == null) {
                //TODO: save generated key
                tokenGroup.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
                logger?.debug(Base64.getEncoder().encodeToString(tokenGroup.signingKey?.encoded))
                tokenGroupRepository.save(tokenGroup.toDao())
            }


            val jwtString = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("alg", "HS256")
                    .setIssuedAt(Date())
                    .signWith(tokenGroup.signingKey)
                    .compact()

            //TODO: log generated token
            return TokenDto(jwtString)
        }
        else
            return TokenDto()
    }

    fun createTokenGroup(name : String){
        val tokenGroup = TokenGroup()
        tokenGroup.name = name
        tokenGroup.uuid = UUID.randomUUID()
        tokenGroup.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

        tokenGroupRepository.save(tokenGroup.toDao())
    }
}