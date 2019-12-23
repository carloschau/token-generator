package carloschau.tokengenerator.util

import carloschau.tokengenerator.model.user.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

@Component
class JwtTokenUtil @Autowired constructor(
    @Value("\${authentication.key.filename}")
    val authKeyFilename : String
){
    private val logger = LoggerFactory.getLogger(javaClass)

    //TODO: Read key from file
    private val key = Keys.keyPairFor(SignatureAlgorithm.ES256)
    fun getJwt(user: User, expirationDate : Date, accessToken : String) : String
    {
        //TODO: Use JWE when jsonwebtoken support it
        return Jwts.builder()
                    .setIssuedAt(Date())
                    .setSubject(user.username)
                    .setId(accessToken)
                    .setExpiration(expirationDate)
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .compact()
    }

    private val privateKey get() : PrivateKey{
        val authKey = ResourceUtils.getFile("classpath:$authKeyFilename")
        val base64String = authKey.readText()
                .replace(Regex("-----.+-----"), "")
                .replace("\n", "")
        val byteArray = Base64.getDecoder().decode(base64String)
        val keyFactory = KeyFactory.getInstance("EC")
        val privSpec = PKCS8EncodedKeySpec(byteArray)
        return keyFactory.generatePrivate(privSpec)
    }
}