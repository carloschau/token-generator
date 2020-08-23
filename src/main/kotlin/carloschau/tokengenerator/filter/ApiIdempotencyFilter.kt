package carloschau.tokengenerator.filter

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ApiIdempotencyFilter : HttpFilter() {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?) {

        logger.info("preHandle")

        //TODO: If API Idempotency header exists, check if stored in db
        if (request.getHeader(apiIdempotencyHeaderName) != null){
            //TODO: If yes, write stored response

            return
        }

        val responseWrapper = ContentCachingResponseWrapper(response)
        chain?.doFilter(request, responseWrapper)

        val shouldStoreResponse = HttpMethod.POST.matches(request.method) && request.getHeader(apiIdempotencyHeaderName) != null

        val responseBytes = responseWrapper.contentAsByteArray
        responseWrapper.copyBodyToResponse()
        logger.info("status code: ${response.status}, header: ${response.headerNames}, body: ${responseBytes.toString(Charsets.UTF_8)}")

        if (shouldStoreResponse){
            //TODO: If API Idempotency header exists
            //TODO: Store response in db
        }

    }



    companion object {
        private const val apiIdempotencyHeaderName : String = "Idempotency-Key"
    }

}