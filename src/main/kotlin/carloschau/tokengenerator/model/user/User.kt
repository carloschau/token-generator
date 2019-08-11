package carloschau.tokengenerator.model.user


import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*


enum class UserStatus{
    INACTIVE, ACTIVE, LOCKED, DELETED
}

class User (
        val Id : String? = null,
        var username : String = "",
        var email : String = "",
        var passwordHash : String = "",
        var status : UserStatus? = null,
        var createdOn : Date? = null){

    val toDao get() = UserDao(
            Id,
            username,
            email,
            passwordHash,
            status?.name,
            createdOn
    )

    companion object{
        fun fromDao(dao : UserDao) : User{
            return User(
                    dao.Id,
                    dao.username,
                    dao.email,
                    dao.passwordHash,
                    dao.status?.let {  UserStatus.valueOf(it.toUpperCase()) },
                    dao.createdOn
            )
        }
    }

}

@Document("users")
data class UserDao(@Id val Id: String? = null,
                   @Indexed(unique = true) val username: String,
                   val email : String = "",
                   val passwordHash: String = "",
                   val status: String? = null,
                   val createdOn : Date? = null)