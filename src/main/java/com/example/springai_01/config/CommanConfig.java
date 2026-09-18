package com.example.springai_01.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommanConfig {
    @Bean("chatClient")
    public ChatClient chatClient(OllamaChatModel ollamaChatModel){
        return ChatClient.builder(ollamaChatModel).build();
    }

    @Bean("chatClientFlux")
    public ChatClient chatClientFlux(OllamaChatModel ollamaChatModel){
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一个高级程序员")
                .build();
    }
}
