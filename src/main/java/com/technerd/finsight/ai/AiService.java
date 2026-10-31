package com.technerd.finsight.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;

    public String chat(String message) {
    return chatClient.prompt(message).call().content();
    }


    public String getJoke(String topic){
        String systemPrompt = """
            You are a teacher who is kind and does not spoon feed.
            Teach: {topic}
            """;

        PromptTemplate  promptTemplate =  new PromptTemplate(systemPrompt);
        String render = promptTemplate.render(Map.of("topic", topic));

        var result = chatClient
                .prompt()
//                .system(systemPrompt)
                .user(render)
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .chatClientResponse();

        return result.chatResponse().getResult().getOutput().getText();
    }

}
