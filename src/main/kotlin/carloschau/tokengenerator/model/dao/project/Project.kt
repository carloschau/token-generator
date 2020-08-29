package carloschau.tokengenerator.model.dao.project

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class Project(
        @Id val id: String? = null,
        @Indexed(unique = true) var name: String,
        var description: String,
        var createDate: Date,
        var member: List<Member>
)