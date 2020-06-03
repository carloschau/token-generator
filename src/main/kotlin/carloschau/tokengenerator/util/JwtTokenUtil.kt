package carloschau.tokengenerator.util

import io.jsonwebtoken.*
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
    fun getJwt(claims: Map<String, Any>) : String
    {
        //TODO: Use JWE when jsonwebtoken support it
        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(Date())
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact()
    }

    fun parseClaimsJws(jwt: String): Jws<Claims>?{
        return try {
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(jwt)
        }
        catch (e: JwtException){
            logger.error(e.toString())
            null
        }
    }

    private val privateKey get() : PrivateKey{
        val byteArray = getByteArrayByFilename(authKeyFilename)
        val keyFactory = KeyFactory.getInstance("EC")
        val privSpec = PKCS8EncodedKeySpec(byteArray)
        return keyFactory.generatePrivate(privSpec)
    }

    private val publicKey get() : PublicKey{
        val byteArray = getByteArrayByFilename(publicKeyFilename)
        val keyFactory = KeyFactory.getInstance("EC")
        val publicKeySpec = X509EncodedKeySpec(byteArray)
        return keyFactory.generatePublic(publicKeySpec)
    }

    private fun getByteArrayByFilename(filename: String) : ByteArray{
        val authKey = ResourceUtils.getFile("classpath:$filename")
        val base64String = authKey.readText()
                .replace(Regex("-----.+-----"), "")
                .replace("\n", "")
                .replace("\r", "")
        return Base64.getDecoder().decode(base64String)
    }
}