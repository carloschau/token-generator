package carloschau.tokengenerator.model.authentication

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

class AuthenticationToken(val token : String, val userId : String, val issueAt: Date, val expiration : Date, val accessToken : String,
                          val device : String, var isValid: Boolean = true) {
    var id : String? = null
    val toDao get() =
        AuthenticationTokenDao(
                id,
                token,
                userId,
                issueAt,
                expiration,
                accessToken,
                device,
                isValid
        )

    companion object{
        fun fromDao(dao: AuthenticationTokenDao?) : AuthenticationToken?{
            return dao?.run {
                AuthenticationToken(
                        token,
                        userId,
                        issueAt,
                        expiration,
                        accessToken,
                        device,
                        isValid
                ).apply { id = dao.Id }
            }
        }
    }
}

@Document(collection = "authenticationToken")
data class AuthenticationTokenDao(
        @Id
        var Id:String? = null,
        var token : String,
        var userId : String,
        var issueAt : Date,
        var expiration : Date,
        var accessToken : String,
        var device : String,
        var isValid : Boolean
)