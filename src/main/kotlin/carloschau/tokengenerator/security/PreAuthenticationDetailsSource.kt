package carloschau.tokengenerator.security

import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.core.authority.GrantedAuthoritiesContainer
import javax.servlet.http.HttpServletRequest

class PreAuthenticationDetailsSource : AuthenticationDetailsSource<HttpServletRequest, GrantedAuthoritiesContainer> {
    override fun buildDetails(context: HttpServletRequest?): GrantedAuthoritiesContainer {
        TODO("Not yet implemented")
    }
}