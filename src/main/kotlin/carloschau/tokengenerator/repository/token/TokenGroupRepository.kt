package carloschau.tokengenerator.repository.token

import carloschau.tokengenerator.model.dao.token.TokenGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface TokenGroupRepository : MongoRepository<TokenGroup, String>, TokenGroupRepositoryCustom {
    fun findByUuid(uuid: UUID) : TokenGroup?
    fun findByOwnerId(ownerId: String): List<TokenGroup>
    fun findAllByProjectId(projectId: String) : List<TokenGroup>
}

interface TokenGroupRepositoryCustom{
    fun incNumberOfTokenIssued(id: String)
}

class TokenGroupRepositoryCustomImpl: TokenGroupRepositoryCustom{

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    override fun incNumberOfTokenIssued(id: String) {
        val query = query(where("id").`is`(id))
        val update  = Update().inc("numberOfTokenIssued",1)

        mongoTemplate.findAndModify(query, update, TokenGroup::class.java)
    }

}
