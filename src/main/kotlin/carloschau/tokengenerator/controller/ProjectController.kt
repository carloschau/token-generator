package carloschau.tokengenerator.controller

import carloschau.tokengenerator.model.dto.request.project.CreateProject
import carloschau.tokengenerator.model.dto.response.project.Project
import carloschau.tokengenerator.security.AuthenticationDetails
import carloschau.tokengenerator.service.TokenProjectService
import carloschau.tokengenerator.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.query.Param
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
@RequestMapping("/projects")
class ProjectController {

    @Autowired
    lateinit var tokenProjectService: TokenProjectService

    @Autowired
    lateinit var userService: UserService

    @PostMapping
    fun createProject(@RequestBody @Valid createProject: CreateProject){
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails
        val newRole = tokenProjectService.createProject(
                createProject.name, createProject.description,
                authenticationDetails.userId)
        authenticationDetails.roles.add(newRole)
    }

    @PutMapping("/{projectName}")
    @PreAuthorize("isProjectOwner(#projectName) || isProjectAdmin(#projectName)")
    fun updateProject(@PathVariable @Param("projectName") projectName: String){

    }

    @GetMapping("/{projectName}")
    @PreAuthorize("isProjectMember(#projectName)")
    fun getProject(@PathVariable @Param("projectName") projectName: String): Project{
        return tokenProjectService.getProjectByName(projectName)?.let {
            Project(it.name, it.description, it.createDate)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Project $projectName not exists")
    }

    @DeleteMapping("/{projectName}")
    @PreAuthorize("isProjectOwner(#projectName)")
    fun deleteProject(@PathVariable @Param("projectName") projectName: String){
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails
        tokenProjectService.removeProject(projectName)
        authenticationDetails.roles.removeIf { role -> role.directory == projectName }
    }

    @GetMapping
    fun getAllProjectByUser(pageable: Pageable): List<Project>{
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails
        return tokenProjectService.getProjectsByUserId(authenticationDetails.userId, pageable).map {
            Project(it.name, it.description, it.createDate)
        }
    }

    @PostMapping("/{projectName}/member")
    fun addMember(){

    }

    @GetMapping("/{projectName}/members")
    fun getAllProjectMembers(){

    }

    @GetMapping("/{projectName}/member/{userId}")
    fun getProjectMember(){

    }

    @PutMapping("/{projectName}/member")
    fun updateMember(){

    }

    @DeleteMapping("/{projectName}/member")
    fun removeMember(){

    }

    @GetMapping("/{projectName}/token")
    fun getAllToken(){

    }
}