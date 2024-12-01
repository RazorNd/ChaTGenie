package ru.razornd.ai.chatgenie

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.ai.autoconfigure.chat.client.ChatClientAutoConfiguration
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import ru.razornd.ai.chatgenie.GenerateRequest.Message


@ImportAutoConfiguration(ChatClientAutoConfiguration::class)
@SpringBootTest(classes = [SuggestionService::class, ChatClientConfiguration::class])
class SuggestionServiceTest {

    @MockitoBean
    private lateinit var chatModel: ChatModel

    @Autowired
    private lateinit var service: SuggestionService

    @Test
    fun `generateSuggestion should return correct suggestion for user message`() {
        val request = GenerateRequest(
            listOf(
                Message(GenerateRequest.Type.USER, "Hello Assistant!")
            )
        )
        val expectedContent = "Hello User!"

        doReturn(ChatResponse(listOf(Generation(AssistantMessage(expectedContent)))))
            .`when`(chatModel).call(any(Prompt::class.java))

        val result = service.generateSuggestion(request)

        assertThat(result).isEqualTo(expectedContent)
        verify(chatModel).call(
            Prompt(
                SystemMessage(ClassPathResource("/prompts/suggestion.prompt.txt")),
                UserMessage("Hello Assistant!")
            )
        )
    }

    @Test
    fun `generateSuggestion should handle empty messages list`() {
        val generateRequest = GenerateRequest(emptyList())

        val result = service.generateSuggestion(generateRequest)

        assertThat(result).isNull()
        verifyNoInteractions(chatModel)
    }

}
