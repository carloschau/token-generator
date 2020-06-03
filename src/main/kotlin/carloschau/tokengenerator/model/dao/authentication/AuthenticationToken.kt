package carloschau.tokengenerator.model.dao.authentication

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class AuthenticationToken(
        @Id
        var id:String? = null,
        var userId : String,
        var issueAt : Date,
        var expiration : Date,
        var accessToken : UUID,
        var device : String,
        var isValid : Boolean = true
)