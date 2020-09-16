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
        val tokenGroup = TokenGroup(
            name = createTokenGroup.name,
            uuid = UUID.randomUUID(),
            ownerId = userId,
            effectiveFrom = createTokenGroup.effectiveFrom,
            effectiveTo = createTokenGroup.effectiveTo,
            maxTokenIssuance = createTokenGroup.maxTokenIssuance,
            pattern = createTokenGroup.pattern,
            projectId = ""
        ).apply {
            signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
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

    fun findAllTokenGroupByProject(projectId: String): List<TokenGroup>{
        return tokenGroupRepository.findAllByProjectId(projectId)
    }
}