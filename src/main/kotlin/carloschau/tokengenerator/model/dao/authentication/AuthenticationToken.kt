package carloschau.tokengenerator.model.dao.authentication

import java.util.*

data class AuthenticationToken(
        var issueAt : Date,
        var expiration : Date,
        var accessToken : UUID,
        var device : String,
        var isValid : Boolean = true
)