package carloschau.tokengenerator.model.dto.response.token

import carloschau.tokengenerator.model.dao.token.TokenGroup
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
            tokenGroup.id,
            tokenGroup.name,
            tokenGroup.ownerId,
            tokenGroup.numberOfTokenIssued,
            tokenGroup.maxTokenIssuance,
            tokenGroup.effectiveFrom,
            tokenGroup.effectiveTo,
            tokenGroup.uuid?.toString(),
            tokenGroup.signingKey?.encoded.let { Base64.getEncoder().encodeToString(it) }
    )
}