package carloschau.tokengenerator.interceptor

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpMethod
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter
import java.lang.Exception
import java.lang.reflect.Type
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class PostRequestIdempotencyInterceptor : RequestBodyAdviceAdapter() {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun beforeBodyRead(inputMessage: HttpInputMessage, parameter: MethodParameter, targetType: Type, converterType: Class<out HttpMessageConverter<*>>): HttpInputMessage {
        //        logger.info("preHandle")
//        if (!HttpMethod.POST.matches(request.method))
//            return true
//
//        //TODO: If API Idempotency header exists, check if stored in db
//        if (request.getHeader(apiIdempotencyHeaderName) != null){
//
//            //TODO: If yes, return stored response
//
//            return false
//        }
//
//
//        return true
        return inputMessage
    }

    override fun afterBodyRead(body: Any, inputMessage: HttpInputMessage, parameter: MethodParameter, targetType: Type, converterType: Class<out HttpMessageConverter<*>>): Any {
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType)
    }

//    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

//    }
//
//    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
//        logger.info("postHandle")
//        if (!HttpMethod.POST.matches(request.method))
//            return
//
//        modelAndView
//        logger.info("status code: ${response.status}, header: ${response.headerNames}, body: ${response}")
//        //TODO: If API Idempotency header exists
//            //TODO: Store response in db
//    }
//
//    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
//        super.afterCompletion(request, response, handler, ex)
//    }

    companion object {
        private const val apiIdempotencyHeaderName : String = "Idempotency-Key"
    }

    override fun supports(methodParameter: MethodParameter, targetType: Type, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        TODO("Not yet implemented")
    }
}