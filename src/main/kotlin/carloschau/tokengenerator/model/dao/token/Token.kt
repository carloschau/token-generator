package carloschau.tokengenerator.model.dao.token

import carloschau.tokengenerator.model.token.TokenType
import io.jsonwebtoken.security.Keys
import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*
import javax.crypto.SecretKey

@Document
data class Token (
        @Id
        var id: String? = null,
        var uuid: UUID? = null,
        var media: String? = null,
        var type: TokenType = TokenType.TEXT,
        var issueAt: Date = Date(),
        var expireAt: Date? = null,
        var isActive: Boolean = true,
        var disableDetail: TokenDisableDetail?  = null,
        var groupInfo: TokenGroupInfo = TokenGroupInfo(),
        var meta: Map<String, String> = mapOf()
)

data class TokenDisableDetail(
        var action: TokenDisableAction,
        var date: Date
)

class TokenGroupInfo{
        var groupId: String? = null
        var pattern: String? = null

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
}

enum class TokenDisableAction{
        CONSUMED,
        REVOKED
}