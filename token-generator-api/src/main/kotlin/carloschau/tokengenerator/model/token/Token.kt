package carloschau.tokengenerator.model.token

import carloschau.tokengenerator.dto.model.token.TokenDto
import java.util.*

class Token(val Id : String? = null, var jwt : String = "", var tokenGroup_Id: String? = null, val createdOn : Date = Date()){
    val toDto get() = TokenDto(jwt)
}