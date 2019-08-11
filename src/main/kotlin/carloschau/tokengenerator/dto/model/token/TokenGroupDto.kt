package carloschau.tokengenerator.dto.model.token

import java.util.*

data class TokenGroupDto (val Id: String? = null,
                          val name : String = "",
                          val owner_Id : String? = null,
                          val numberOfTokenIssued : Int = 0,
                          val maxTokenIssuance : Int = 0,
                          val effectiveDate : Date? = null,
                          val expiryDate : Date? = null,
                          val uuid : String? = null,
                          val signingKey : String? = null)