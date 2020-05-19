package carloschau.tokengenerator.repository.token

import carloschau.tokengenerator.model.authentication.AuthenticationTokenDao
import org.springframework.data.mongodb.repository.MongoRepository

interface AuthenticationTokenRepository : MongoRepository<AuthenticationTokenDao, String>{
}