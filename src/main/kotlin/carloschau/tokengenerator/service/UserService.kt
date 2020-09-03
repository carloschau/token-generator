package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.user.RoleAuthority
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

    fun authenticate(username : String, password : String) : User? {
        val emailRegex =
                "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".toRegex()
        val user =
                if (emailRegex.matches(username))
                    userRepository.findByEmail(username)
                else
                    userRepository.findByUsername(username)
        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

        return if (user != null && argon2.verify(user.passwordHash, password)) user else null
    }

}