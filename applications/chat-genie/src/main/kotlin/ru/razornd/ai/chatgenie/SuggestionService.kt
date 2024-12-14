package ru.razornd.ai.chatgenie

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class SuggestionService(private val chatClient: ChatClient, private val repository: UserSystemTextRepository) {

    fun generateSuggestion(userId: String, request: GenerateRequest): String? {
        if (request.messages.isEmpty()) {
            log.debug { "No messages provided in the request." }
            return null
        }

        log.atDebug {
            message = "Received generate request: ${request.messages.last().text}"
            payload = mapOf("request" to request.toMap())
        }

        val messages = request.messages.map { message ->
            when (message.type) {
                GenerateRequest.Type.USER -> UserMessage(message.text)
                GenerateRequest.Type.ASSISTANT -> AssistantMessage(message.text)
            }
        }

        val content = chatClient.prompt()
            .messages(messages)
            .advisors { it.param(UserSystemTextAdvisor.USER_ID, userId) }
            .call()
            .content()

        log.atDebug {
            message = "Generated suggestion result: $content"
            payload = mapOf("content" to content)
        }

        return content
    }

    fun systemText(userId: String): String = repository.getByUserId(userId)?.systemText ?: ""

    fun updateSystemText(userId: String, newText: String) = repository.updateSystemText(userId, newText)

    private fun GenerateRequest.toMap(): Collection<Map<String, Any>> {
        return messages.map { message ->
            mapOf("type" to message.type, "text" to message.text)
        }
    }
}
