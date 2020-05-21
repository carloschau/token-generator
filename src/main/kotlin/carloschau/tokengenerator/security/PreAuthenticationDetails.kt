package carloschau.tokengenerator.security

import carloschau.tokengenerator.model.user.UserDao
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthoritiesContainer

class PreAuthenticationDetails(val user : UserDao) : GrantedAuthoritiesContainer {
    val roles : MutableList<RoleAuthority> = user.roles.map {
        RoleAuthority(it)
    }.toMutableList()
    val username : String = user.username

    override fun getGrantedAuthorities(): MutableCollection<out GrantedAuthority> {
        return roles
    }
}

class RoleAuthority(val role: String) : GrantedAuthority{
    override fun getAuthority(): String {
        return role
    }

}