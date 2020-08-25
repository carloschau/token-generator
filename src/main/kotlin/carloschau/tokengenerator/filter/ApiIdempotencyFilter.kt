package carloschau.tokengenerator.filter

import carloschau.tokengenerator.service.ApiIdempotencyService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private lateinit var apiIdempotencyService: ApiIdempotencyService

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?) {
        val apiIdempotencyKey : String? = request.getHeader(apiIdempotencyHeaderName)
        if (HttpMethod.POST.matches(request.method) && apiIdempotencyKey != null && apiIdempotencyService.isIdempotencyKeyExists(apiIdempotencyKey)){
            logger.info("${request.method} request with API Idempotency Key: $apiIdempotencyKey found stored, returning stored content & status instead of processing the request.")
            apiIdempotencyService.getIdempotencyRecord(apiIdempotencyKey)?.run {
                response.outputStream.write(content)
                response.status = status
                response.flushBuffer()
            }
            return
        }

        val responseWrapper = ContentCachingResponseWrapper(response)
        chain?.doFilter(request, responseWrapper)


        val responseBytes = responseWrapper.contentAsByteArray
        responseWrapper.copyBodyToResponse()

        if (HttpMethod.POST.matches(request.method) && apiIdempotencyKey != null){
            logger.info("${request.method} request with new API Idempotency Key: $apiIdempotencyKey, response info will stored")
            apiIdempotencyService.addIdempotencyRecord(
                    apiIdempotencyKey,
                    response.status,
                    responseBytes
            )
        }
    }

    companion object {
        private const val apiIdempotencyHeaderName : String = "Idempotency-Key"
    }

}