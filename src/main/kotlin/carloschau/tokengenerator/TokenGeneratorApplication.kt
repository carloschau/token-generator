package carloschau.tokengenerator

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class TokenGeneratorApplication

fun main(args: Array<String>) {
	runApplication<TokenGeneratorApplication>(*args)
}