package carloschau.tokengenerator.model.dao.idempotency

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class IdempotencyRecord (
        @Id
        val key: String,
        val status: Int,
        val content: ByteArray
)