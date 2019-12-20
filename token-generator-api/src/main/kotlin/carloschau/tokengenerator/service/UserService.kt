package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.user.User
import carloschau.tokengenerator.model.user.UserStatus
import carloschau.tokengenerator.repository.user.UsersRepository
import de.mkammerer.argon2.Argon2Factory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class  UserService{

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var userRepository: UsersRepository

    fun createUser(username : String, email : String, password : String){

        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
        val hashedPassword = argon2.hash(2, 1024 * 1024, 4, password);

        val user = User(
            null,
            username,
            email,
            hashedPassword,
            UserStatus.INACTIVE,
            Date()
        )

        userRepository.save(user.toDao)
    }

    fun authenticate(email : String, password : String) : User? {
        val user = User.fromDao(userRepository.findByEmail(email))

        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

        return if (user != null && argon2.verify(user.passwordHash, password)) user else null
    }

}