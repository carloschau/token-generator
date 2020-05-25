package carloschau.tokengenerator.security

import carloschau.tokengenerator.model.dao.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthoritiesContainer

class AuthenticationDetails(user : User) : GrantedAuthoritiesContainer {
    val roles : MutableList<RoleAuthority> = user.roles.map {
        RoleAuthority(it)
    }.toMutableList()
    val username : String = user.username
    val userId : String = user.id ?: ""

    override fun getGrantedAuthorities(): MutableCollection<out GrantedAuthority> {
        return roles
    }
}

class RoleAuthority(val role: String) : GrantedAuthority{
    override fun getAuthority(): String {
        return role
    }

}