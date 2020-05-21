package carloschau.tokengenerator.util

import carloschau.tokengenerator.model.user.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Component
class JwtTokenUtil{
    @Value("\${authentication.key.filename}")
    private lateinit var authKeyFilename : String
    private val publicKeyFilename : String = "authkey_pub_uat.pem"

    private val logger = LoggerFactory.getLogger(javaClass)
    //TODO: generalize function parameter
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

    fun getSubject(jwt: String): String{
        return parseClaimsJws(jwt).body.subject
    }

    fun parseClaimsJws(jwt: String): Jws<Claims>{
        return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(jwt)
    }


    private val privateKey get() : PrivateKey{
        val authKey = ResourceUtils.getFile("classpath:$authKeyFilename")
        val base64String = authKey.readText()
                .replace(Regex("-----.+-----"), "")
                .replace("\n", "")
                .replace("\r", "")
        val byteArray = Base64.getDecoder().decode(base64String)
        val keyFactory = KeyFactory.getInstance("EC")
        val privSpec = PKCS8EncodedKeySpec(byteArray)
        return keyFactory.generatePrivate(privSpec)
    }

    private val publicKey get() : PublicKey{
        val authKey = ResourceUtils.getFile("classpath:$publicKeyFilename")
        val base64String = authKey.readText()
                .replace(Regex("-----.+-----"), "")
                .replace("\n", "")
                .replace("\r", "")
        val byteArray = Base64.getDecoder().decode(base64String)
        val keyFactory = KeyFactory.getInstance("EC")
        val publicKeySpec = X509EncodedKeySpec(byteArray)
        return keyFactory.generatePublic(publicKeySpec)
    }
}