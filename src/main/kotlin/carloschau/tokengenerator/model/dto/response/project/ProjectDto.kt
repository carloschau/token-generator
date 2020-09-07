package carloschau.tokengenerator.model.dto.response.project

import java.util.*

data class ProjectDto(
        val name: String,
        val description: String,
        val createDate: Date
)