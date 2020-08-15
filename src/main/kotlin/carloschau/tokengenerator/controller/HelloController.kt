package carloschau.tokengenerator.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello")
class HelloController{
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    @GetMapping
    fun hello(): String{
        logger.info("hello")
        return "hello"
    }
}