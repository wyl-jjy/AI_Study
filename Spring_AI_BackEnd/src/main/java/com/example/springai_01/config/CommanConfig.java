package com.example.springai_01.config;

import com.example.springai_01.constant.FunctionCallingPrompt;
import com.example.springai_01.constant.PromptWord;
import com.example.springai_01.tools.CourseTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommanConfig {
    /**
     * 该类中配置的chatClient的bean对象是springAI提供给应用层和chatmodel交互的端口。
     * controller配置的是一次从应用向ChatClient 发起的请求，chatClient再转发给大模型。
     */

    /**
     * 记忆容器：1.1.x 起 InMemoryChatMemory 已移除，改用 MessageWindowChatMemory。
     * 只保留最近 20 条消息，避免上下文无限增长。
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }

    @Bean("chatClient")
    public ChatClient chatClient(OllamaChatModel ollamaChatModel, ChatMemory chatMemory) {
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean("chatClientFlux")
    public ChatClient chatClientFlux(OllamaChatModel ollamaChatModel, ChatMemory chatMemory) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一个高级程序员")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean("chatClientPrompt")
    public ChatClient chatClientPrompt(OllamaChatModel ollamaChatModel, ChatMemory chatMemory) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem(PromptWord.GAME_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean("chatFunctionCalling")
    public ChatClient chatFunctionCalling(OllamaChatModel ollamaChatModel,
                                          ChatMemory chatMemory,
                                          CourseTool courseTool) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem(FunctionCallingPrompt.SERVICE_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(courseTool)
                .build();
    }

    @Bean
    public VectorStore vectorStore(OllamaEmbeddingModel ollamaEmbeddingModel) {
        return SimpleVectorStore.builder(ollamaEmbeddingModel).build();
    }

}
