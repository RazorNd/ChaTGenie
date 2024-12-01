package ru.razornd.ai.chatgenie

import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [SuggestionController::class])
class SuggestionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var suggestionService: SuggestionService

    @Test
    fun `should return suggestion response when valid request`() {
        val requestBody = """
                {
                    "messages": [
                        {"type": "user", "text": "Sample user message"},
                        {"type": "assistant", "text": "Sample assistant response"}
                    ]
                }               
                """.trimIndent()
        val expectedRequest = GenerateRequest(
            listOf(
                GenerateRequest.Message(GenerateRequest.Type.USER, "Sample user message"),
                GenerateRequest.Message(GenerateRequest.Type.ASSISTANT, "Sample assistant response")
            )
        )
        val expectedResponse = "Test suggestion"

        doReturn(expectedResponse).`when`(suggestionService).generateSuggestion(expectedRequest)

        mockMvc.perform(
            post("/api/generate/suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpectAll(
            status().isOk,
            content().json("""{"text":"$expectedResponse"}""")
        )
    }

}
