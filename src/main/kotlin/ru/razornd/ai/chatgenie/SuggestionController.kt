package ru.razornd.ai.chatgenie

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

data class GenerateRequest(val messages: Collection<Message>) {

    data class Message(val type: Type, val text: String)

    enum class Type {
        @JsonProperty("assistant")
        ASSISTANT,

        @JsonProperty("user")
        USER
    }
}

data class GenerateResponse(val text: String)

@CrossOrigin("http://localhost:1234", "https://chat-genie.razornd.ru")
@RestController
@RequestMapping("/api/generate/suggestions")
class SuggestionController(private val chatClient: ChatClient) {

    @PostMapping
    fun generate(@RequestBody request: GenerateRequest): GenerateResponse {
        log.atDebug {
            message = "Received generate request: ${request.messages.last().text}"
            payload = mapOf("request" to request)
        }

        val messages = request.messages.map { message ->
            when (message.type) {
                GenerateRequest.Type.USER -> UserMessage(message.text)
                GenerateRequest.Type.ASSISTANT -> AssistantMessage(message.text)
            }
        }

        val response = GenerateResponse(
            chatClient.prompt()
                .messages(messages)
                .call()
                .content() ?: ""
        )

        log.atDebug {
            message = "Generated suggestion result: ${response.text}"
            payload = mapOf("response" to response)
        }

        return response

    }

}
