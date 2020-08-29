package carloschau.tokengenerator.model.dao.project

import carloschau.tokengenerator.model.dao.project.role.Role

data class Member(
        val userId: String,
        val role: Role
)
