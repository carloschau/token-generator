package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.project.Member
import carloschau.tokengenerator.model.dao.project.Project
import carloschau.tokengenerator.model.dao.project.role.Role
import carloschau.tokengenerator.model.dao.user.RoleAuthority
import carloschau.tokengenerator.repository.project.ProjectRepository
import carloschau.tokengenerator.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenProjectService {
    @Autowired
    private lateinit var projectRepository : ProjectRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    fun createProject(projectName: String, description: String, ownerUserId: String): RoleAuthority{
        projectRepository.insert(Project(
                name = projectName,
                description = description,
                member = listOf(Member(ownerUserId, Role.Owner)),
                createDate = Date()
        ))
        val newRole = RoleAuthority(projectName, Role.Owner.name)
        userRepository.pushRoleAuthority(ownerUserId, newRole)
        return newRole
    }

    fun removeProject(projectName: String){
        projectRepository.deleteByName(projectName)
        userRepository.pullRoleAuthorityByDirectory(projectName)
    }
}