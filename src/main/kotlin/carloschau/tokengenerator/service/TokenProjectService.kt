package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.project.Project
import carloschau.tokengenerator.model.dao.project.role.Role
import carloschau.tokengenerator.model.dao.user.RoleAuthority
import carloschau.tokengenerator.model.dao.user.User
import carloschau.tokengenerator.repository.project.ProjectRepository
import carloschau.tokengenerator.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class TokenProjectService {
    @Autowired
    private lateinit var projectRepository : ProjectRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Transactional
    fun createProject(projectName: String, description: String, ownerUserId: String): RoleAuthority{
        val project = projectRepository.insert(Project(
                name = projectName,
                description = description,
                createDate = Date()
        ))
        val newRole = RoleAuthority(project.id!!, Role.Owner.name)
        userRepository.pushRoleAuthority(ownerUserId, newRole)
        return newRole
    }

    @Transactional
    fun removeProject(projectName: String){
        getProjectByName(projectName)?.let {
            userRepository.pullRoleAuthorityByDirectory(it.id!!)
        }
        projectRepository.deleteByName(projectName)
    }

    fun getProjectByName(projectName: String): Project?{
        return projectRepository.findByName(projectName)
    }

    fun getProjectsByUserId(userId: String, pageable: Pageable): List<Project>{
        return userRepository.findByIdOrNull(userId)?.let {
            val projectIds = it.roles.map { r -> r.directory }
            projectRepository.findByIdIn(projectIds, pageable)
        } ?: listOf()
    }

    fun updateProject(projectName: String, newName: String, newDescription: String){
        projectRepository.updateByName(projectName, mapOf(
                "name" to newName,
                "description" to newDescription
        ))
    }

    fun addMemberToProject(projectId: String, userId: String, role: Role){
        userRepository.pushRoleAuthority(userId, RoleAuthority(projectId, role.name))
    }

    fun getAllProjectMember(projectId: String, pageable: Pageable) : List<User> {
        return userRepository.findAllByRoles_Directory(projectId, pageable)
    }

    fun isProjectExists(projectName: String) : Boolean{
        return projectRepository.existsByName(projectName)
    }

    @Transactional
    fun updateProjectOwnerShip(projectId: String, oldOwnerId: String, newOwnerId: String){
        userRepository.updateRoleAuthorityByDirectory(oldOwnerId, RoleAuthority(projectId, Role.Admin.name))
        userRepository.updateRoleAuthorityByDirectory(newOwnerId, RoleAuthority(projectId, Role.Owner.name))
    }

    fun updateMemberRole(projectId: String, userId: String, role: Role) {
        userRepository.updateRoleAuthorityByDirectory(userId, RoleAuthority(projectId, role.name))
    }

    fun removeMemberFromProject(projectId: String, userId: String) {
        userRepository.pullRoleAuthority(userId, projectId)
    }
}