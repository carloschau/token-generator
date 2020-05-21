package carloschau.tokengenerator.repository.token

import carloschau.tokengenerator.model.dao.authentication.AuthenticationToken
import org.springframework.data.mongodb.repository.MongoRepository

interface AuthenticationTokenRepository : MongoRepository<AuthenticationToken, String>{
    fun findByAccessToken(accessToken : String) : AuthenticationToken?
}