package carloschau.tokengenerator.model.dao.token

import carloschau.tokengenerator.util.UuidUtil
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
    var effectiveDate: Date? = null
    var expiryDate : Date? = null
    @Transient
    var uuid: UUID? = null
    set(value) {
        uuidBinary = value?.let { UuidUtil.toBytes(it) }?.let { Binary(it) }
        field = value
    }
    get()  {
        field = uuidBinary?.let { UUID.nameUUIDFromBytes(it.data) }
        return field
    }

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

    @Field("uuid")
    private var uuidBinary: Binary? = null

    @Field("signingKey")
    private var signingKeyBinary: Binary? = null
}