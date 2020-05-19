package carloschau.tokengenerator.repository.token

import carloschau.tokengenerator.model.token.Token
import org.springframework.data.mongodb.repository.MongoRepository

interface TokenRepository : MongoRepository<Token, String>{

}