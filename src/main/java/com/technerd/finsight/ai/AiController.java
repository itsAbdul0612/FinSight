package com.technerd.finsight.ai;

import com.technerd.finsight.ai.contextbuilder.FinanceContextBuilderService;
import com.technerd.finsight.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;
    private final FinanceContextBuilderService financeContextBuilderService;



    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return aiService.chat(message);
    }
}
