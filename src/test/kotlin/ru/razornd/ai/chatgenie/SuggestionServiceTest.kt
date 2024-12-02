package ru.razornd.ai.chatgenie

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
@SpringBootTest(classes = [SuggestionService::class, ChatClientConfiguration::class, UserSystemTextAdvisor::class])
class SuggestionServiceTest {

    @MockitoBean
    private lateinit var chatModel: ChatModel

    @MockitoBean
    private lateinit var repository: UserSystemTextRepository

    @Autowired
    private lateinit var service: SuggestionService

    private val expectedContent = "Hello User!"

    @BeforeEach
    fun setUp() {
        lenient().doReturn(ChatResponse(listOf(Generation(AssistantMessage(expectedContent)))))
            .`when`(chatModel).call(any(Prompt::class.java))
    }

    @Test
    fun `generateSuggestion should return correct suggestion for user message`() {
        val request = GenerateRequest(
            listOf(
                Message(GenerateRequest.Type.USER, "Hello Assistant!")
            )
        )

        val result = service.generateSuggestion("testUserId", request)

        assertThat(result).isEqualTo(expectedContent)
        verify(chatModel).call(
            Prompt(
                SystemMessage(ClassPathResource("/prompts/suggestion.prompt.txt")),
                UserMessage("Hello Assistant!")
            )
        )
    }

    @Test
    fun `generateSuggestion should return correct suggestion for user message with custom system text`() {
        val userId = "5438"
        val expectSystemText = "text system text"
        val request = GenerateRequest(
            listOf(
                Message(GenerateRequest.Type.USER, "Hello Assistant!")
            )
        )

        doReturn(UserSystemText(userId, expectSystemText)).`when`(repository).getByUserId(userId)

        val result = service.generateSuggestion(userId, request)

        assertThat(result).isEqualTo(expectedContent)
        verify(chatModel).call(
            Prompt(
                SystemMessage(expectSystemText),
                UserMessage("Hello Assistant!")
            )
        )
    }

    @Test
    fun `generateSuggestion should handle empty messages list`() {
        val generateRequest = GenerateRequest(emptyList())

        val result = service.generateSuggestion("testUserId", generateRequest)

        assertThat(result).isNull()
        verifyNoInteractions(chatModel)
    }

    @Test
    fun `getSystemText should return correct system text for given user ID`() {
        val userId = "5438"
        val expectedSystemText = "expected system text"

        doReturn(UserSystemText(userId, expectedSystemText)).`when`(repository).getByUserId(userId)

        val systemText = service.systemText(userId)

        assertThat(systemText).isEqualTo(expectedSystemText)
    }

    @Test
    fun `updateSystemText should correctly update system text for given user ID`() {
        val userId = "5438"
        val newSystemText = "new system text"

        service.updateSystemText(userId, newSystemText)

        verify(repository).updateSystemText(userId, newSystemText)
    }

}
