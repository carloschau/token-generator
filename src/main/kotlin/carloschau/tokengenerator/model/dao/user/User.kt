package carloschau.tokengenerator.model.dao.user


import carloschau.tokengenerator.model.dao.authentication.AuthenticationToken
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*


enum class UserStatus{
    INACTIVE, ACTIVE, LOCKED, DELETED
}

@Document
data class User(
        @Id val id: String? = null,
        @Indexed(unique = true) val username: String,
        @Indexed(unique = true) val email : String,
        val passwordHash: String,
        val status: UserStatus,
        val createdOn : Date,
        val roles : List<RoleAuthority> = listOf(),
        var authenticationTokens : List<AuthenticationToken> = listOf()
)