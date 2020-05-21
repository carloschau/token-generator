package carloschau.tokengenerator.security

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter

class TokenAuthenticationFilter : RequestHeaderAuthenticationFilter() {

    init {
        setPrincipalRequestHeader(AUTHORIZATION)
        setExceptionIfHeaderMissing(false)
    }
}

