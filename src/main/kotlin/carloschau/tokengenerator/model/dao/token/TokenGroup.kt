package carloschau.tokengenerator.model.dao.token

import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class TokenGroup(
        @Id
        var Id: String? = null,
        var name: String = "",
        var owner_Id: String? = null,
        var numberOfTokenIssued: Int = 0,
        var maxTokenIssuance: Int = 0,
        var effectiveDate: Date? = null,
        var expiryDate : Date? = null,
        var uuid: Binary? = null,
        var signingKey : Binary? = null)
