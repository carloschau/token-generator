package carloschau.tokengenerator.security

import carloschau.tokengenerator.service.AuthenticationService
import carloschau.tokengenerator.util.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.GrantedAuthoritiesContainer
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class PreAuthenticationDetailsSource : AuthenticationDetailsSource<HttpServletRequest, GrantedAuthoritiesContainer> {
    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    override fun buildDetails(context: HttpServletRequest): GrantedAuthoritiesContainer {
        return context.let {
            val headerVal = context.getHeader(authenticationHeader)
            val jwtStr = if (headerVal.startsWith(BEARER, true))
                            headerVal.replace(BEARER, "", true)
                        else
                            throw PreAuthenticatedCredentialsNotFoundException(
                                    "{} header not starts with {}".format(authenticationHeader, BEARER))

            jwtTokenUtil.parseClaimsJws(jwtStr)?.let {jws ->
                val accessToken = jws.body.id
                val user =  authenticationService.getUserByAccessToken(accessToken)
                user?.let {
                    AuthenticationDetails(user)
                } ?: throw BadCredentialsException("User not found by access token")
            } ?: throw BadCredentialsException("Authentication token expired")
        }
    }

    companion object {
        private const val BEARER : String = "Bearer "
        private const val authenticationHeader = AUTHORIZATION
    }
}