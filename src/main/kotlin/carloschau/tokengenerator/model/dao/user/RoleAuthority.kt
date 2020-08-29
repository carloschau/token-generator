package carloschau.tokengenerator.model.dao.user

import org.springframework.security.core.GrantedAuthority

class RoleAuthority (
        val directory: String,
        val role: String
) : GrantedAuthority{
    override fun getAuthority(): String {
        return "$directory/$role"
    }
}