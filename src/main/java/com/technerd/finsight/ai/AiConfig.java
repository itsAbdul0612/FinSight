package com.technerd.finsight.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.RedisClient;

import java.time.Duration;

@Configuration
public class AiConfig {

    @Bean
    public RedisChatMemoryRepository redisChatMemoryRepository(RedisClient jedisClient) {
        return RedisChatMemoryRepository.builder()
                .jedisClient(jedisClient)
                .timeToLive(Duration.ofMinutes(40))
                .maxMessagesPerConversation(400)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient
                .builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @Bean
    public ChatMemory chatMemory(RedisChatMemoryRepository redisChatMemoryRepository) {
        return MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(redisChatMemoryRepository)
                .maxMessages(30)
                .build();
    }
}
