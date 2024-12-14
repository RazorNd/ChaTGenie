package ru.razornd.ai.chatgenie

import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/generate/suggestions")
class SuggestionController(private val service: SuggestionService) {

    @PostMapping
    fun generate(
        @RequestParam userId: String,
        @RequestBody request: GenerateRequest
    ) = TextResponse(service.generateSuggestion(userId, request) ?: "")

    @GetMapping("/system-text")
    fun getSystemText(@RequestParam userId: String) = TextResponse(service.systemText(userId))

    @ResponseStatus(NO_CONTENT)
    @PutMapping("/system-text")
    fun updateSystemText(
        @RequestParam userId: String,
        @RequestBody updateText: UpdateText
    ) = service.updateSystemText(userId, updateText.newText)

}
