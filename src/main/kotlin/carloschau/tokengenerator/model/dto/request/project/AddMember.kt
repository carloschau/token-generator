package carloschau.tokengenerator.model.dto.request.project

import carloschau.tokengenerator.model.dao.project.role.Role

data class AddMember (val memberId: String, val role: Role = Role.Contributor)