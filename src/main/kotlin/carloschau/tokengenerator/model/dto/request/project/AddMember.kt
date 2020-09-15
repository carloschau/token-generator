package carloschau.tokengenerator.model.dto.request.project

import carloschau.tokengenerator.model.dao.project.role.Role

data class AddMember (
        val userId: String,
        val role: Role = Role.Contributor
)