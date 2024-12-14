import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    kotlin("jvm") version "2.0.21" apply false
    id("org.springframework.boot") version "3.4.0" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}

allprojects {
    group = "ru.razornd.ai"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        configure<KotlinJvmProjectExtension> {
            compilerOptions {
                freeCompilerArgs.addAll("-Xjsr305=strict")
            }
        }
    }


    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<BootBuildImage> {
        val registryUrl: String? by project
        val registryUsername: String? by project
        val registryPassword: String? by project
        val imageVersion: String? by project

        val domain = listOfNotNull(registryUrl, registryUsername?.lowercase(), project.name.lowercase()).joinToString("/")

        imageName = "$domain:${imageVersion ?: project.version}"

        docker {
            publishRegistry {
                url = registryUrl
                username = registryUsername
                password = registryPassword
            }
        }
    }

}
