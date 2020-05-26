package carloschau.tokengenerator.model.dao.token

import carloschau.tokengenerator.model.token.TokenType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class Token (
    @Id
    var id: String? = null,
    var uuid: UUID? = null,
    var groupId: String? = null,
    var media: String? = null,
    var type: TokenType = TokenType.Text,
    var issueAt: Date = Date(),
    var expireAt: Date? = null,
    var isActive: Boolean = true
)