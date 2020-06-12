package carloschau.tokengenerator.service.token

import carloschau.tokengenerator.model.dao.token.TokenGroup
import carloschau.tokengenerator.model.dto.request.token.CreateTokenGroup
import carloschau.tokengenerator.repository.token.TokenGroupRepository
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class TokenGroupService {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var tokenGroupRepository: TokenGroupRepository

    fun createTokenGroup(createTokenGroup : CreateTokenGroup, userId: String): String{
        val tokenGroup = TokenGroup().apply {
            this.name = createTokenGroup.name
            this.uuid = UUID.randomUUID()
            this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
            this.ownerId = userId
            this.effectiveFrom = createTokenGroup.effectiveFrom
            this.effectiveTo = createTokenGroup.effectiveTo
            this.maxTokenIssuance = createTokenGroup.maxTokenIssuance
            this.pattern = createTokenGroup.pattern
        }

        if (!tokenGroup.pattern.isNullOrEmpty() && !tokenGroup.validatePattern())
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Token pattern is not empty but not valid")

        logger.info("Token group uuid created: ${ tokenGroup.uuid }")
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