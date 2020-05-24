package carloschau.tokengenerator.repository.user

import carloschau.tokengenerator.model.dao.user.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByEmail(email : String) : User?
    fun findByUsername(username : String) : User?
}
