package com.technerd.finsight.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return aiService.chat(message);
    }
}
