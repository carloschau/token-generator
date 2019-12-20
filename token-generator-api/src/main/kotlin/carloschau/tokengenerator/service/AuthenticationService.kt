package carloschau.tokengenerator.service

import carloschau.tokengenerator.dto.model.user.UserDto
import carloschau.tokengenerator.model.authentication.AuthenticationToken
import carloschau.tokengenerator.model.user.User
import carloschau.tokengenerator.repository.token.AuthenticationTokenRepository
import carloschau.tokengenerator.util.JwtTokenUtil
import carloschau.tokengenerator.util.UuidUtil
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class AuthenticationService {
    @Autowired
    private lateinit var authenticationTokenRepository: AuthenticationTokenRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${authentication.token.expirationTime}")
    lateinit var TOKEN_EXPIRE_SECONDS : String

    fun IssueAuthenticationToken(user : UserDto, userAgent : String) : String
    {
        val expiration = Date(System.currentTimeMillis() + TOKEN_EXPIRE_SECONDS.toInt() * 1000)
        val accessToken = UUID.randomUUID().toString()
        val jwt = JwtTokenUtil.getJwt(user, expiration, accessToken)

        val authenticationToken = AuthenticationToken(jwt, user.Id ?: "", Date(), expiration, accessToken, userAgent)

        //Log the token
        authenticationTokenRepository.insert(authenticationToken.toDao)
        logger.debug("Authentication token created, ${0} for user ${1}", authenticationToken.accessToken, authenticationToken.userId)
        return jwt
    }

    //Verify token

    //Revoke token
}