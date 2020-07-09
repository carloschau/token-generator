package carloschau.tokengenerator.repository.token

import carloschau.tokengenerator.model.dao.token.Token
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface TokenRepository : MongoRepository<Token, String>{
    fun findByUuid(uuid: UUID): Token?
}