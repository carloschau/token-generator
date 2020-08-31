package carloschau.tokengenerator.controller

import carloschau.tokengenerator.model.dao.project.role.Role
import carloschau.tokengenerator.model.dao.user.RoleAuthority
import carloschau.tokengenerator.model.dto.request.project.CreateProject
import carloschau.tokengenerator.security.AuthenticationDetails
import carloschau.tokengenerator.service.TokenProjectService
import carloschau.tokengenerator.service.UserService
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/project")
class ProjectController {
    lateinit var tokenProjectService: TokenProjectService
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
    fun updateProject(){

    }

    @GetMapping("/{projectName}")
    fun getProject(){

    }

    @DeleteMapping("/{projectName}")
    @PreAuthorize("hasAnyAuthority([#projectName'/Owner'])")
    fun deleteProject(@PathVariable("projectName") projectName: String){

    }

    @GetMapping("/projects")
    fun getAllProjectByUser(){

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