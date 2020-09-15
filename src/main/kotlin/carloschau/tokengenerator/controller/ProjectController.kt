package carloschau.tokengenerator.controller

import carloschau.tokengenerator.model.dto.request.project.AddMember
import carloschau.tokengenerator.model.dto.request.project.CreateProject
import carloschau.tokengenerator.model.dto.request.project.UpdateProject
import carloschau.tokengenerator.model.dto.response.project.Member
import carloschau.tokengenerator.model.dto.response.project.ProjectDto
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
    fun updateProject(
            @PathVariable @Param("projectName") projectName: String,
            @RequestBody updateProject: UpdateProject
    ){
        tokenProjectService.updateProject(projectName, updateProject.name, updateProject.description)
    }

    @GetMapping("/{projectName}")
    @PreAuthorize("isProjectMember(#projectName)")
    fun getProject(@PathVariable @Param("projectName") projectName: String): ProjectDto{
        return tokenProjectService.getProjectByName(projectName)?.let {
            ProjectDto(it.name, it.description, it.createDate)
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
    fun getAllProjectByUser(pageable: Pageable): List<ProjectDto>{
        val authenticationDetails = SecurityContextHolder.getContext().authentication.details as AuthenticationDetails
        return tokenProjectService.getProjectsByUserId(authenticationDetails.userId, pageable).map {
            ProjectDto(it.name, it.description, it.createDate)
        }
    }

    @PostMapping("/{projectName}/members")
    @PreAuthorize("isProjectOwner(#projectName) || isProjectAdmin(#projectName)")
    fun addMember(@PathVariable @Param("projectName") projectName: String, @RequestBody @Valid addMember: AddMember){
        val project = tokenProjectService.getProjectByName(projectName)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found.")

        val user = userService.findUser(addMember.userId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")

        if (user.roles.any { r -> r.directory == projectName })
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a member.")

        tokenProjectService.addMemberToProject(projectName, addMember.userId, addMember.role)
    }

    @GetMapping("/{projectName}/members")
    @PreAuthorize("isProjectMember(#projectName)")
    fun getAllProjectMembers(@PathVariable @Param("projectName") projectName: String, pageable: Pageable) : List<Member>{
        if (!tokenProjectService.isProjectExists(projectName))
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found.")

        return tokenProjectService.getAllProjectMember(projectName, pageable).map {
            Member(
                    it.id!!,
                    it.username,
                    it.email,
                    it.roles.first { r -> r.directory == projectName }.role
            )
        }
    }

    @GetMapping("/{projectName}/member/{userId}")
    fun getProjectMember(@PathVariable @Param("projectName") projectName: String){

    }

    @PutMapping("/{projectName}/member")
    fun updateMember(@PathVariable @Param("projectName") projectName: String){

    }

    @DeleteMapping("/{projectName}/member")
    fun removeMember(@PathVariable @Param("projectName") projectName: String){

    }

    @GetMapping("/{projectName}/token")
    fun getAllToken(@PathVariable @Param("projectName") projectName: String){

    }
}