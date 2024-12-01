package ru.razornd.ai.chatgenie

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/generate/suggestions")
class SuggestionController(private val service: SuggestionService) {

    @PostMapping
    fun generate(@RequestBody request: GenerateRequest) = GenerateResponse(service.generateSuggestion(request) ?: "")

}
