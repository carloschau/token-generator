package carloschau.tokengenerator.dto.model.user

import carloschau.tokengenerator.model.user.UserStatus
import java.util.*

data class UserDto (
    val Id: String?,
    val username : String,
    val email :String,
    val status: UserStatus,
    val createdOn: Date
)