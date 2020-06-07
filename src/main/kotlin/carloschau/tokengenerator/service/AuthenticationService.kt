package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.authentication.AuthenticationToken
import carloschau.tokengenerator.model.dao.user.User
import carloschau.tokengenerator.model.dao.user.UserStatus
import carloschau.tokengenerator.repository.token.AuthenticationTokenRepository
import carloschau.tokengenerator.repository.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService {
    @Autowired
    private lateinit var authenticationTokenRepository: AuthenticationTokenRepository
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
                userId = user.id!!,
                issueAt = Date(),
                expiration = expiration,
                accessToken = accessToken,
                device = userAgent)

        //Log the token
        authenticationTokenRepository.insert(authenticationToken)
        logger.info("Authentication token created, ${authenticationToken.accessToken} for user ${authenticationToken.userId}")
        return authenticationToken
    }

    //Verify token
    fun getUserByAccessToken(accessToken: String): User?
    {
        val authenticationToken = authenticationTokenRepository.findByAccessToken(UUID.fromString(accessToken))
        return authenticationToken?.let {
            userRepository.findById(authenticationToken.userId).orElse(null)?.let { user ->
                if (user.status == UserStatus.ACTIVE)
                    user
                else
                    null
            }
        }
    }

    //Revoke token
}