package carloschau.tokengenerator.response.model.token

import carloschau.tokengenerator.model.token.TokenGroup
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
{
    constructor(tokenGroup : TokenGroup) : this(
            tokenGroup.Id,
            tokenGroup.name,
            tokenGroup.owner_Id,
            tokenGroup.numberOfTokenIssued,
            tokenGroup.maxTokenIssuance,
            tokenGroup.effectiveDate,
            tokenGroup.expiryDate,
            tokenGroup.uuid?.toString(),
            tokenGroup.signingKey?.encoded.let { Base64.getEncoder().encodeToString(it) }
    )
}