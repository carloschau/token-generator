package carloschau.tokengenerator.repository.token

import carloschau.tokengenerator.model.token.TokenGroupDao
import org.bson.types.Binary
import org.springframework.data.mongodb.repository.MongoRepository

interface TokenGroupRepository : MongoRepository<TokenGroupDao, String> {

    fun findByUuid(uuid: Binary) : TokenGroupDao?
}