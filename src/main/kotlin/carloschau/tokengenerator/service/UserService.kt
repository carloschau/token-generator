package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.user.User
import carloschau.tokengenerator.model.dao.user.UserStatus
import carloschau.tokengenerator.repository.user.UserRepository
import de.mkammerer.argon2.Argon2Factory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class  UserService{

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var userRepository: UserRepository

    fun createUser(username : String, email : String, password : String){

        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
        val hashedPassword = argon2.hash(2, 1024 * 1024, 4, password);

        val user = User(
            username = username,
            email = email,
            passwordHash = hashedPassword,
            status = UserStatus.INACTIVE,
            createdOn = Date()
        )

        userRepository.save(user)
    }

    fun authenticate(email : String, password : String) : User? {
        val user = userRepository.findByEmail(email)
        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

        return if (user != null && argon2.verify(user.passwordHash, password)) user else null
    }

}