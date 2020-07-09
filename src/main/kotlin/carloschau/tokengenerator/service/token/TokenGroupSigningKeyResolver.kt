package carloschau.tokengenerator.service.token

import carloschau.tokengenerator.repository.token.TokenGroupRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.SigningKeyResolverAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.security.Key

class TokenGroupSigningKeyResolver(private val tokenGroupRepository: TokenGroupRepository) : SigningKeyResolverAdapter() {
    override fun resolveSigningKey(header: JwsHeader<*>, claims: Claims): Key {
        val groupId = header[JwsHeader.KEY_ID] as String
        return tokenGroupRepository.findByIdOrNull(groupId)!!.signingKey!!
    }
}