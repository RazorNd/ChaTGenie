package ru.razornd.ai.chatgenie

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.testcontainers.ollama.OllamaContainer
import org.testcontainers.utility.DockerImageName

@Import(DataBaseTestcontainersConfiguration::class)
@TestConfiguration(proxyBeanMethods = false)
open class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    open fun ollamaContainer(): OllamaContainer {
        return OllamaContainer(DockerImageName.parse("ollama/ollama:latest"))
    }

}
