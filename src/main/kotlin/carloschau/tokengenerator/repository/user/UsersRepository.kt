package carloschau.tokengenerator.repository.user

import carloschau.tokengenerator.model.user.UserDao
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository


interface UsersRepository{
    fun findByEmail(email : String) : UserDao?
    fun pushAuthenticationAccessToken(userId : String, accessToken : String)
    fun save(user: UserDao)
}

@Repository
class UsersRepositoryImp : UsersRepository{
    @Autowired
    lateinit var mongoTemplate : MongoTemplate

    override fun findByEmail(email: String): UserDao? {
        return mongoTemplate.findOne(Query.query(Criteria.where("email").`is`(email)), UserDao::class.java)
    }

    override fun pushAuthenticationAccessToken(userId : String, accessToken : String){
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").`is`(ObjectId(userId))),
                Update().push("accessTokens", accessToken), UserDao::class.java)
    }

    override fun save(user: UserDao) {
        mongoTemplate.save(user)
    }
}

