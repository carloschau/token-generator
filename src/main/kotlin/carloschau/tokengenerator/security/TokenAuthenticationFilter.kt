package carloschau.tokengenerator.security

import carloschau.tokengenerator.util.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import javax.servlet.http.HttpServletRequest

class TokenAuthenticationFilter : AbstractPreAuthenticatedProcessingFilter() {
    private val BEARER : String = "Bearer "

    @Autowired
    lateinit var jwtUtil: JwtTokenUtil

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest?): Any? {
        return getAuthorizationHeader(request)
    }

    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest?): Any? {
        return getAuthorizationHeader(request)?.let {
            PreAuthenticatedPrincipal(jwtUtil.getSubject(it))
        }
    }

    private fun getAuthorizationHeader(request: HttpServletRequest?) : String?{
        return request?.let {
            val authorizeHeader= request.getHeader(AUTHORIZATION)

            if (!authorizeHeader.isNullOrEmpty() && authorizeHeader.startsWith(BEARER))
                authorizeHeader.removePrefix(BEARER)
            else
                null
        }
    }


}

