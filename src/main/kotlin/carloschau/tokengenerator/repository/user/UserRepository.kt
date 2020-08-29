package carloschau.tokengenerator.repository.user

import carloschau.tokengenerator.model.dao.authentication.AuthenticationToken
import carloschau.tokengenerator.model.dao.user.RoleAuthority
import carloschau.tokengenerator.model.dao.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.MongoRepository
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
    fun pushRoleAuthority(userId: String, role: RoleAuthority)
    fun updateRoleAuthorityByDirectory(userId: String, role: RoleAuthority)
}

class UserRepositoryCustomImpl:  UserRepositoryCustom{
    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    override fun pushAuthenticationToken(userId: String, authenticationToken: AuthenticationToken) {
        val query = query(where("id").`is`(userId))
        val update = Update().push("authenticationTokens", authenticationToken)
        mongoTemplate.updateFirst(query, update, User::class.java).modifiedCount
    }

    override fun pushRoleAuthority(userId: String, role: RoleAuthority){
        val query = query(where("id").`is`(userId))
        val update = Update().push("roles", role)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updateRoleAuthorityByDirectory(userId: String, role: RoleAuthority){
        val query = query(where("id").`is`(userId).and("roles.directory").`is`(role.directory))
        val update = Update().set("roles.$.role", role.role)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }
}
