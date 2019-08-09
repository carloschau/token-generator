package carloschau.tokengenerator.repository.token

import carloschau.tokengenerator.model.token.TokenGroup
import carloschau.tokengenerator.model.token.TokenGroupDao
import org.bson.BsonBinary
import org.bson.types.Binary
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface TokenGroupRepository : MongoRepository<TokenGroupDao, String> {

    fun findByUuid(uuid: Binary) : TokenGroupDao?
}