package carloschau.tokengenerator.model.token

import carloschau.tokengenerator.util.UuidUtil
import io.jsonwebtoken.security.Keys
import org.bson.BsonBinary
import org.bson.types.Binary
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.annotation.Generated
import javax.crypto.SecretKey

class TokenGroup (
    var Id: String? = null,
    var name : String = "",
    var owner : Nothing? = null,
    var numberOfTokenIssued : Int = 0,
    var maxTokenIssuance : Int = 0,
    var effectiveDate : Date? = null,
    var expiryDate : Date? = null,
    var uuid : UUID? = null,
    var signingKey : SecretKey? = null
){
    fun toDao() : TokenGroupDao{
        return TokenGroupDao(
                Id,
                name,
                owner,
                numberOfTokenIssued,
                maxTokenIssuance,
                effectiveDate,
                expiryDate,
                uuid?.let{ UuidUtil.toStandardBinaryUUID(it) },
                Binary(signingKey?.encoded)
        )
    }

    companion object{
        fun fromDao(dao : TokenGroupDao?) : TokenGroup?{
            return dao?.let {
                TokenGroup(
                    dao.Id,
                    dao.name,
                    dao.owner,
                    dao.numberOfTokenIssued,
                    dao.maxTokenIssuance,
                    dao.effectDate,
                    dao.expiryDate,
                    dao.uuid?.let { UuidUtil.fromStandardBinaryUUID(it) },
                    dao.signingKey?.data?.let {  Keys.hmacShaKeyFor(it) }
                )
            }
        }
    }

}

@Document(collection = "tokenGroups")
data class TokenGroupDao(
        @Id
        var Id: String? = null,
        var name: String = "",
        var owner: Nothing? = null,
        var numberOfTokenIssued: Int = 0,
        var maxTokenIssuance: Int = 0,
        var effectDate: Date? = null,
        var expiryDate : Date? = null,
        var uuid: Binary? = null,
        var signingKey : Binary? = null)
