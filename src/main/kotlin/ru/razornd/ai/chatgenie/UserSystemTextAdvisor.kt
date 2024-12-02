package ru.razornd.ai.chatgenie

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain
import org.springframework.stereotype.Component


@Component
class UserSystemTextAdvisor(private val repository: UserSystemTextRepository) : CallAroundAdvisor {
    override fun aroundCall(request: AdvisedRequest, chain: CallAroundAdvisorChain): AdvisedResponse {
        val userId = request.adviseContext[USER_ID]?.toString()

        val (_, systemText) = loadUserSystemText(userId) ?: return chain.nextAroundCall(request)

        return chain.nextAroundCall(
            AdvisedRequest.from(request)
                .withSystemText(systemText)
                .build()
        )
    }

    override fun getOrder() = 0

    override fun getName() = "UserSystemText"

    private fun loadUserSystemText(userId: String?): UserSystemText? {
        return repository.getByUserId(userId ?: return null)
    }

    companion object {
        const val USER_ID = "chat_user_system_text_user_id"
    }
}
