package carloschau.tokengenerator.service.token

import carloschau.tokengenerator.repository.token.TokenGroupRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.SigningKeyResolverAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.security.Key

@Component
class TokenGroupSigningKeyResolver : SigningKeyResolverAdapter() {
    @Autowired
    private lateinit var tokenGroupRepository: TokenGroupRepository

    override fun resolveSigningKey(header: JwsHeader<out JwsHeader<*>>?, claims: Claims?): Key {
        val groupId = header!!.keyId
        return tokenGroupRepository.findByIdOrNull(groupId)!!.signingKey!!
    }
}