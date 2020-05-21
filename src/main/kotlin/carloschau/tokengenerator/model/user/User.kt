package carloschau.tokengenerator.model.user


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

    val accessTokens = listOf<String>()

    val toDao get() = UserDao(
            Id,
            username,
            email,
            passwordHash,
            status.name,
            accessTokens,
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

@Document("user")
data class UserDao(@Id val Id: String?,
                   @Indexed(unique = true) val username: String,
                   @Indexed(unique = true) val email : String,
                   val passwordHash: String,
                   val status: String,
                   val accessTokens : List<String> = listOf(),
                   val createdOn : Date,
                    val roles : List<String> = listOf())