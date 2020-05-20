package carloschau.tokengenerator.model.token

import carloschau.tokengenerator.response.model.token.TokenGroupDto
import carloschau.tokengenerator.util.UuidUtil
import io.jsonwebtoken.security.Keys
import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.crypto.SecretKey

class TokenGroup (
    var Id: String? = null,
    var name : String = "",
    var owner_Id : String? = null,
    var numberOfTokenIssued : Int = 0,
    var maxTokenIssuance : Int = 0,
    var effectiveDate : Date? = null,
    var expiryDate : Date? = null,
    var uuid : UUID? = null,
    var signingKey : SecretKey? = null
){
    val toDao get()  =
        TokenGroupDao(
            Id,
            name,
            owner_Id,
            numberOfTokenIssued,
            maxTokenIssuance,
            effectiveDate,
            expiryDate,
            uuid?.let { UuidUtil.toBytes(it) }?.let { Binary(it) },
            Binary(signingKey?.encoded)
        )

    companion object{
        fun fromDao(dao : TokenGroupDao?) : TokenGroup?{
            return dao?.let {
                TokenGroup(
                    dao.Id,
                    dao.name,
                    dao.owner_Id,
                    dao.numberOfTokenIssued,
                    dao.maxTokenIssuance,
                    dao.effectDate,
                    dao.expiryDate,
                    dao.uuid?.let { UUID.nameUUIDFromBytes(it.data) },
                    dao.signingKey?.data?.let {  Keys.hmacShaKeyFor(it) }
                )
            }
        }
    }

}

@Document(collection = "tokenGroup")
data class TokenGroupDao(
        @Id
        var Id: String? = null,
        var name: String = "",
        var owner_Id: String? = null,
        var numberOfTokenIssued: Int = 0,
        var maxTokenIssuance: Int = 0,
        var effectDate: Date? = null,
        var expiryDate : Date? = null,
        var uuid: Binary? = null,
        var signingKey : Binary? = null)
