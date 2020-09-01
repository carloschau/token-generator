package carloschau.tokengenerator.security.expression

import carloschau.tokengenerator.model.dao.project.role.Role
import org.springframework.security.access.expression.SecurityExpressionRoot
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations
import org.springframework.security.core.Authentication

class CustomMethodSecurityExpressionRoot(authentication: Authentication) : SecurityExpressionRoot(authentication), MethodSecurityExpressionOperations {

    /*MethodSecurityExpressionRoot Re-implementation*/
    private var filterObject: Any? = null
    private var returnObject: Any? = null
    private var target: Any? = null

    override fun setFilterObject(filterObject: Any) {
        this.filterObject = filterObject
    }

    override fun getFilterObject(): Any? {
        return filterObject
    }

    override fun setReturnObject(returnObject: Any) {
        this.returnObject = returnObject
    }

    override fun getReturnObject(): Any? {
        return returnObject
    }

    /**
     * Sets the "this" property for use in expressions. Typically this will be the "this"
     * property of the `JoinPoint` representing the method invocation which is being
     * protected.
     *
     * @param target the target object on which the method in is being invoked.
     */
    fun setThis(target: Any) {
        this.target = target
    }

    override fun getThis(): Any? {
        return target
    }

    /*MethodSecurityExpressionRoot Re-implementation*/

    fun isProjectOwner(projectName: String): Boolean{
        return hasAnyAuthority(getProjectAuthority(projectName, Role.Owner))
    }

    fun isProjectAdmin(projectName: String): Boolean{
        return hasAnyAuthority(getProjectAuthority(projectName, Role.Admin))
    }

    fun isProjectContributor(projectName: String): Boolean{
        return hasAnyAuthority(getProjectAuthority(projectName, Role.Contributor))
    }

    fun isProjectReviewer(projectName: String): Boolean{
        return hasAnyAuthority(getProjectAuthority(projectName, Role.Reviewer))
    }

    fun isProjectMember(projectName: String): Boolean{
        val roleAuthorities = Role.values().map { r -> getProjectAuthority(projectName, r) }.toTypedArray()
        return hasAnyAuthority(*roleAuthorities)
    }

    private fun getProjectAuthority(projectName: String, role: Role): String{
        return "$projectName/${role.name}"
    }
}