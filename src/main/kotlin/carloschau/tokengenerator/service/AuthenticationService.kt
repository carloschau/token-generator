package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.authentication.AuthenticationToken
import carloschau.tokengenerator.model.user.User
import carloschau.tokengenerator.model.user.UserDao
import carloschau.tokengenerator.model.user.UserStatus
import carloschau.tokengenerator.repository.token.AuthenticationTokenRepository
import carloschau.tokengenerator.repository.user.UserRepository
import carloschau.tokengenerator.util.JwtTokenUtil
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

    @Autowired
    private  lateinit var jwtTokenUtil : JwtTokenUtil

    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${authentication.token.expirationTime}")
    lateinit var TOKEN_EXPIRE_SECONDS : String

    fun issueAuthenticationToken(user : User, userAgent : String) : AuthenticationToken
    {
        val expiration = Date(System.currentTimeMillis() + TOKEN_EXPIRE_SECONDS.toInt() * 1000)
        val accessToken = UUID.randomUUID().toString()
        val jwt = jwtTokenUtil.getJwt(user, expiration, accessToken)

        val authenticationToken = AuthenticationToken(jwt, user.Id ?: "", Date(), expiration, accessToken, userAgent)

        //Log the token
        authenticationTokenRepository.insert(authenticationToken.toDao)
        logger.info("Authentication token created, ${authenticationToken.accessToken} for user ${authenticationToken.userId}")
        return authenticationToken
    }

    //Verify token
    fun getUserByAccessToken(accessToken: String): UserDao?
    {
        val authenticationToken = authenticationTokenRepository.findByAccessToken(accessToken)
        return authenticationToken?.let {
            val user = userRepository.findById(authenticationToken.userId)
            user?.let {
                if (user.status == UserStatus.ACTIVE.name)
                    user
                else
                    null
            }
        }
    }

    //Revoke token
}