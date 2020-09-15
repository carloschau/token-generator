package carloschau.tokengenerator.model.dao.token

import carloschau.tokengenerator.constant.token.TokenPatternPlaceholder
import carloschau.tokengenerator.util.CommonUtil
import io.jsonwebtoken.security.Keys
import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*
import javax.crypto.SecretKey

@Document
class TokenGroup()
{
    @Id
    var id: String? = null
    var name: String = ""
    var ownerId: String? = null
    var numberOfTokenIssued: Int = 0
    var maxTokenIssuance: Int = 0
    var effectiveFrom: Date? = null
    var effectiveTo : Date? = null
    var uuid: UUID? = null
    var tokenLifetime : Int = 0
    var isActive : Boolean = true
    var pattern : String? = null


    @Transient
    var signingKey: SecretKey? = null
    set(value) {
        signingKeyBinary = Binary(value?.encoded)
        field = value
    }
    get() {
        field = signingKeyBinary?.data?.let {  Keys.hmacShaKeyFor(it) }
        return field
    }

    @Field("signingKey")
    private var signingKeyBinary: Binary? = null

    val canIssueToken : Boolean get() =
            isActive && !isGroupExpired && !isGroupIneffectiveYet && !isMaxTokenIssuanceReached

    val isGroupExpired: Boolean get() = effectiveTo != null && Date().after(effectiveTo)
    val isGroupIneffectiveYet: Boolean get() = effectiveFrom != null && Date().before(effectiveFrom)
    val isMaxTokenIssuanceReached: Boolean get() =
        maxTokenIssuance > 0 && numberOfTokenIssued >= maxTokenIssuance

    val tokenGroupStatus : TokenGroupStatus get() {
        return when {
            canIssueToken -> TokenGroupStatus.VALID
            !isActive -> TokenGroupStatus.INACTIVE
            isGroupExpired -> TokenGroupStatus.EXPIRED
            isGroupIneffectiveYet -> TokenGroupStatus.INEFFECTIVE_YET
            isMaxTokenIssuanceReached -> TokenGroupStatus.MAX_ISSUANCE_REACHED
            else -> TokenGroupStatus.UNKNOWN
        }
    }

    fun validatePattern() : Boolean {
        if (pattern.isNullOrEmpty())
            return false
        else
        {
            val params = pattern!!.let(CommonUtil::tokenizeString)
            if (params.isEmpty())
                return false

            if (params.size > 1 &&
                    params.contains(TokenPatternPlaceholder.EMPTY))
                return false

            if (!params.contains(TokenPatternPlaceholder.EMPTY) &&
                    !params.contains(TokenPatternPlaceholder.TOKEN))
                return false
            
            return true
        }
    }


}

enum class TokenGroupStatus(val reason: String = ""){
    VALID("The token group can issue tokens"),
    INACTIVE("The token group is inactive"),
    EXPIRED("The token group is expired"),
    INEFFECTIVE_YET("The token group is not effective yet"),
    MAX_ISSUANCE_REACHED("The token group reached it's max number of token issuance"),
    UNKNOWN("Unknown reason")
}