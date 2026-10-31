package com.technerd.finsight.ai;

import com.technerd.finsight.ai.contextbuilder.ContextBuilderService;
import com.technerd.finsight.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;
    private final ContextBuilderService  contextBuilderService;


}
