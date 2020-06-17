package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.authentication.AuthenticationToken
import carloschau.tokengenerator.model.dao.user.User
import carloschau.tokengenerator.model.dao.user.UserStatus
import carloschau.tokengenerator.repository.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService {
    @Autowired
    private lateinit var userRepository: UserRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${authentication.token.expirationTime}")
    lateinit var TOKEN_EXPIRE_SECONDS : String

    fun issueAuthenticationToken(user : User, userAgent : String) : AuthenticationToken
    {
        val expiration = Date(System.currentTimeMillis() + TOKEN_EXPIRE_SECONDS.toInt() * 1000)
        val accessToken = UUID.randomUUID()

        val authenticationToken = AuthenticationToken(
                issueAt = Date(),
                expiration = expiration,
                accessToken = accessToken,
                device = userAgent)

        //Log the token
        userRepository.pushAuthenticationToken(user.id!!, authenticationToken)
        logger.info("Authentication token created, ${authenticationToken.accessToken} for user ${user.id}")
        return authenticationToken
    }

    //Verify token
    fun getUserByAccessToken(accessToken: String): User?
    {
        return userRepository.findByAuthenticationTokensAccessToken(UUID.fromString(accessToken))?.let { user ->
            if (user.status == UserStatus.ACTIVE)
                user
            else
                null
        }
    }

    //TODO: Revoke token
}