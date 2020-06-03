package carloschau.tokengenerator.configuration

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoClientURI
import org.bson.UuidRepresentation
import org.bson.codecs.UuidCodec
import org.bson.codecs.configuration.CodecRegistries
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration


@Configuration
@EnableAutoConfiguration
class MongoDbConfig: AbstractMongoConfiguration() {

    @Value("\${spring.data.mongodb.database}")
    lateinit var database: String

    @Value("\${spring.data.mongodb.uri}")
    lateinit var uri: String

    override fun mongoClient(): MongoClient {
        val codecRegistry = CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(UuidCodec(UuidRepresentation.STANDARD)),
                MongoClient.getDefaultCodecRegistry())
        return MongoClient(MongoClientURI(uri,
                MongoClientOptions.builder().codecRegistry(codecRegistry)))
    }

    override fun getDatabaseName(): String {
        return database
    }
}