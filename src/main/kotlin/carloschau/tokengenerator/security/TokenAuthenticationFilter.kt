package carloschau.tokengenerator.security

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter

class TokenAuthenticationFilter : RequestHeaderAuthenticationFilter() {

    init {
        //TODO: Use self-defined constant
        setPrincipalRequestHeader(AUTHORIZATION)
        setExceptionIfHeaderMissing(false)
    }
}

