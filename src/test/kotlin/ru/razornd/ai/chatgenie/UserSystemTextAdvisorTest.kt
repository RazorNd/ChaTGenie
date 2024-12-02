package ru.razornd.ai.chatgenie

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain

@ExtendWith(MockitoExtension::class)
class UserSystemTextAdvisorTest {

    @InjectMocks
    private lateinit var userSystemTextAdvisor: UserSystemTextAdvisor

    @Mock
    private lateinit var repository: UserSystemTextRepository

    @Mock
    private lateinit var chain: CallAroundAdvisorChain

    @Mock
    private lateinit var response: AdvisedResponse

    @Captor
    private lateinit var captor: ArgumentCaptor<AdvisedRequest>

    @BeforeEach
    fun setUp() {
        `when`(chain.nextAroundCall(captor.capture())).thenReturn(response)
    }

    @Test
    fun `should call nextAroundCall with modified request`() {
        val expectedText = "test system text"
        val userId = "57064"
        val initialRequest = AdvisedRequest.builder()
            .withChatModel(mock())
            .withUserText("test user text")
            .withAdviseContext(mapOf(UserSystemTextAdvisor.USER_ID to userId))
            .build()

        doReturn(UserSystemText(userId, expectedText)).`when`(repository).getByUserId(userId)

        val actual = userSystemTextAdvisor.aroundCall(initialRequest, chain)

        assertThat(captor.value).hasFieldOrPropertyWithValue("systemText", expectedText)
        assertThat(actual).isSameAs(response)
    }

    @Test
    fun `should handle missing user ID gracefully`() {
        val initialRequest = AdvisedRequest.builder()
            .withChatModel(mock())
            .withUserText("test user text")
            .build()

        val actual = userSystemTextAdvisor.aroundCall(initialRequest, chain)

        assertThat(captor.value).isSameAs(initialRequest)
        assertThat(actual).isSameAs(response)
        verifyNoInteractions(repository)
    }

    @Test
    fun `should proceed with original request when repository returns null`() {
        val userId = "67890"
        val initialRequest = AdvisedRequest.builder()
            .withChatModel(mock())
            .withUserText("test user text")
            .withAdviseContext(mapOf(UserSystemTextAdvisor.USER_ID to userId))
            .build()

        val actual = userSystemTextAdvisor.aroundCall(initialRequest, chain)

        assertThat(captor.value).isSameAs(initialRequest)
        assertThat(actual).isSameAs(response)
    }
}
