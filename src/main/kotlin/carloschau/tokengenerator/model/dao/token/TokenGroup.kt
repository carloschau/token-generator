package carloschau.tokengenerator.model.dao.token

import io.jsonwebtoken.security.Keys
import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*
import javax.crypto.SecretKey

@Document
class TokenGroup
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

    val canIssueToken : Boolean = isActive &&
            if (effectiveFrom != null && effectiveTo != null)
                Date() in effectiveFrom!!..effectiveTo!!
            else
                true

    @Field("signingKey")
    private var signingKeyBinary: Binary? = null
}