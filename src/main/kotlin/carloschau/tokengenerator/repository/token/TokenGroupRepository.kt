package carloschau.tokengenerator.repository.token

import carloschau.tokengenerator.model.dao.token.TokenGroup
import org.bson.types.Binary
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface TokenGroupRepository : MongoRepository<TokenGroup, String> {

    fun findByUuid(uuid: UUID) : TokenGroup?
    fun findByOwnerId(ownerId: String): List<TokenGroup>
}