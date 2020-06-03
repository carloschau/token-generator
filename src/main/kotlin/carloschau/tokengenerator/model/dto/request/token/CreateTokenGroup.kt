package carloschau.tokengenerator.model.dto.request.token

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class  CreateTokenGroup(
        val name: String,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val effectiveFrom: Date? = null,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val effectiveTo: Date? = null,
        val maxTokenIssuance: Int = 0,
        val tokenLifetime: Int = 0,
        val pattern: String? = null
)