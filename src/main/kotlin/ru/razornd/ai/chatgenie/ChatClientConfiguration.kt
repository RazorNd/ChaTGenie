package ru.razornd.ai.chatgenie

import org.springframework.ai.chat.client.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration(proxyBeanMethods = false)
open class ChatClientConfiguration {

    @Bean
    open fun suggestionChatClient(builder: ChatClient.Builder): ChatClient {
        return builder.defaultSystem(ClassPathResource("/prompts/suggestion.prompt.txt")).build()
    }

}
