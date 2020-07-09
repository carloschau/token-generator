package carloschau.tokengenerator.repository.user

import carloschau.tokengenerator.model.dao.authentication.AuthenticationToken
import carloschau.tokengenerator.model.dao.user.User
import com.mongodb.client.model.Updates.push
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.Update.update
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : MongoRepository<User, String>, UserRepositoryCustom {
    fun findByEmail(email : String) : User?
    fun findByUsername(username : String) : User?
    fun findByAuthenticationTokensAccessToken(accessToken: UUID) : User?
}

interface UserRepositoryCustom {
    fun pushAuthenticationToken(userId: String, authenticationToken: AuthenticationToken)
}

class UserRepositoryCustomImpl:  UserRepositoryCustom{
    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    override fun pushAuthenticationToken(userId: String, authenticationToken: AuthenticationToken) {
        val query = query(where("id").`is`(userId))
        val update = Update().push("authenticationTokens", authenticationToken)
        mongoTemplate.updateFirst(query, update, User::class.java).modifiedCount
    }
}
