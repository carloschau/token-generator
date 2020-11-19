/*
 * This file was generated by the Gradle 'init' task.
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("org.springframework.boot") version "2.1.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("com.bmuschko.docker-spring-boot-application") version "6.5.0"
    war
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
}

group = "carloschau"
version = "0.0.1-SNAPSHOT"
description = "token-generator"
java.sourceCompatibility = JavaVersion.VERSION_1_8
var dockerImageTag = getConfigurationProperty("DOCKER_IMAGE_TAG", "dockerImageTag")

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.41")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:2.1.6.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-web:2.1.6.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-security:2.1.6.RELEASE")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.41")
    implementation("io.jsonwebtoken:jjwt-api:0.11.1")
    implementation("de.mkammerer:argon2-jvm:2.5")
    implementation("com.github.kenglxn.QRGen:javase:2.6.0")
    implementation("org.springdoc:springdoc-openapi-ui:1.4.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.1")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.3.41")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.1.6.RELEASE")
    testImplementation("org.springframework.security:spring-security-test:5.1.5.RELEASE")
}


tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

docker {
    springBootApplication {
        maintainer.set("Carlos Chau 'carlos.chau719@gmail.com'")
        images.set(setOf("${group}/${description}:${dockerImageTag}"))
    }
}

tasks.register<CopyCertificate>("copyCertificate")

tasks.register("getProperty"){
    doLast{
        if (project.hasProperty("propertyName"))
            println(project.findProperty(project.findProperty("propertyName").toString()).toString())
    }
}

fun getConfigurationProperty(envVar: String, sysProp: String): String? {
    return System.getenv(envVar) ?: project.findProperty(sysProp)?.toString()
}

open class CopyCertificate: DefaultTask(){
    private var certFilePath: String? = null
    private var certDestProp: String? = null
    private  var force: Boolean = false

    @Option(option = "cert", description = "Path of certificate file to be copy")
    open fun setCertFilePath(certFilePath: String?) {
        this.certFilePath = certFilePath
    }

    @Option(option = "certDestProperty", description = "Property name which indicates the location of the certificate")
    open fun setCertLocationProp(certDestProp: String?){
        this.certDestProp = certDestProp
    }

    @Option(option = "force", description = "Do force copy if destination file already exists")
    open fun setForce(force: Boolean){
        this.force = force
    }


    @TaskAction
    fun doCopy(){
        if (certFilePath.isNullOrBlank() ||
                certDestProp.isNullOrBlank())
            return

        println("cert Property name: $certDestProp")
        val destPath = System.getProperty(certDestProp!!)?.toString()
        println("destPath: ${project.properties.keys.joinToString()}")
//        if(!file(destPath).exists() || force){
//            ant.withGroovyBuilder {
//                "move"(certFilePath to destPath)
//            }
//        }
    }
}