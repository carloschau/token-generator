package carloschau.tokengenerator.repository.token

import carloschau.tokengenerator.model.dao.token.TokenGroup
import org.bson.types.Binary
import org.springframework.data.mongodb.repository.MongoRepository

interface TokenGroupRepository : MongoRepository<TokenGroup, String> {

    fun findByUuid(uuid: Binary) : TokenGroup?
    fun findByOwnerId(ownerId: String): List<TokenGroup>
}