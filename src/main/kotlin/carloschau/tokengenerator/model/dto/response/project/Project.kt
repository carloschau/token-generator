package carloschau.tokengenerator.model.dto.response.project

import java.util.*

data class Project(
        val name: String,
        val description: String,
        val createDate: Date
)