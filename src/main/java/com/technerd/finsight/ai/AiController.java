package com.technerd.finsight.ai;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
@Tag(name = "AI Assistant", description = "AI-powered personal finance assistance.")
@SecurityRequirement(name = "bearerAuth")
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "Send a chat message", description = "Sends a finance-related message to the AI assistant and returns its reply as plain text.")
    public String chat(@RequestBody String message) {
        return aiService.chat(message);
    }
}
