package com.example.springai_01.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommanConfig {
    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel){
        return ChatClient.builder(ollamaChatModel)
                .build(); //获取ChatClient 工厂实例
    }
}
