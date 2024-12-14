package ru.razornd.ai.chatgenie

import com.fasterxml.jackson.annotation.JsonProperty

data class GenerateRequest(val messages: Collection<Message>) {

    data class Message(val type: Type, val text: String)

    enum class Type {
        @JsonProperty("assistant")
        ASSISTANT,

        @JsonProperty("user")
        USER
    }
}

data class TextResponse(val text: String)

data class UpdateText(val newText: String)
