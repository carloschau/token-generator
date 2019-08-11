package carloschau.tokengenerator.repository.user

import carloschau.tokengenerator.model.user.UserDao
import org.springframework.data.mongodb.repository.MongoRepository

interface UsersRepository : MongoRepository<UserDao, String>{
}