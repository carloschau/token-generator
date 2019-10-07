package carloschau.tokengenerator.model.user


import carloschau.tokengenerator.dto.model.user.UserDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*


enum class UserStatus{
    INACTIVE, ACTIVE, LOCKED, DELETED
}

class User (
        val Id : String?,
        var username : String,
        var email : String,
        var passwordHash : String,
        var status : UserStatus,
        var createdOn : Date){

    val toDao get() = UserDao(
            Id,
            username,
            email,
            passwordHash,
            status.name,
            createdOn
    )

    val toDto get() = UserDto(
            Id,
            username,
            email,
            status,
            createdOn
    )

    companion object{
        fun fromDao(dao : UserDao?) : User?{
            return dao?.let {
                User(
                    dao.Id,
                    dao.username,
                    dao.email,
                    dao.passwordHash,
                    dao.status.let {  UserStatus.valueOf(it.toUpperCase()) },
                    dao.createdOn
                )
            }
        }
    }

}

@Document("users")
data class UserDao(@Id val Id: String?,
                   @Indexed(unique = true) val username: String,
                   @Indexed(unique = true) val email : String,
                   val passwordHash: String,
                   val status: String,
                   val createdOn : Date)