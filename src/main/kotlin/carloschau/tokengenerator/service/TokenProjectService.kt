package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.project.Member
import carloschau.tokengenerator.model.dao.project.Project
import carloschau.tokengenerator.model.dao.project.role.Role
import carloschau.tokengenerator.model.dao.user.RoleAuthority
import carloschau.tokengenerator.repository.project.ProjectRepository
import carloschau.tokengenerator.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
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

    fun getProjectByName(projectName: String): Project?{
        return projectRepository.findByName(projectName)
    }

    fun getProjectsByUserId(userId: String, pageable: Pageable): List<Project>{
        return projectRepository.findAllByMember_UserId(userId, pageable)
    }

    fun updateProject(projectName: String, newName: String, newDescription: String){
        projectRepository.updateByName(projectName, mapOf(
                "name" to newName,
                "description" to newDescription
        ))

        //Handle on project name changed
        if (projectName != newName){
            userRepository.updateRoleDirectory(projectName, newName)
        }
    }
}