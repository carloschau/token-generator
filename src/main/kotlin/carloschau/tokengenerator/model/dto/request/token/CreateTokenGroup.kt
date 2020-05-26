package carloschau.tokengenerator.model.dto.request.token

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

data class  CreateTokenGroup(
        val name: String = "",
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val effectiveDate: Date? = null,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val expiryDate: Date? = null,
        val maxTokenIssuance: Int = 0
)