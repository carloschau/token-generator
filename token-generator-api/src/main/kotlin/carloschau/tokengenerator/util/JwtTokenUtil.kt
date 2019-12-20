package carloschau.tokengenerator.util

import carloschau.tokengenerator.dto.model.user.UserDto
import carloschau.tokengenerator.model.user.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.*

object JwtTokenUtil {
    //TODO: Read key from file
    private val key = Keys.keyPairFor(SignatureAlgorithm.ES256)
    fun getJwt(user: UserDto, expirationDate : Date, accessToken : String) : String
    {
        //TODO: Use JWE when jsonwebtoken support it
        return Jwts.builder()
                    .setIssuedAt(Date())
                    .setSubject(user.username)
                    .setId(accessToken)
                    .setExpiration(expirationDate)
                    .signWith(key.private, SignatureAlgorithm.ES256)
                    .compact()
    }
}