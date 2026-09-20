package com.example.springai_01.controller;

import com.example.springai_01.Interface.ChatHistoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@CrossOrigin("*")
@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    @Qualifier("chatClient")
    private ChatClient chatClient;
    @Autowired
    @Qualifier("chatClientFlux")
    private ChatClient chatClientFlux;
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @RequestMapping("/chat")
    public String chat(@RequestParam(defaultValue = "讲个笑话") String prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    /**
     * 流式对话，同时输出思考内容与正式回答。
     * <p>
     * 输出协议（前端按前缀解析）：
     * <ul>
     *   <li>{@code <think>...</think>} —— 模型思考片段，供折叠展示</li>
     *   <li>其余文本 —— 正式回答</li>
     * </ul>
     * 注意：prompt 设为非必填，否则前端"只传附件不输文字"会被 Spring 直接拒绝（400）。
     */
    @RequestMapping(path = "/chat-flux", produces = "text/html;charset=UTF-8")
    public Flux<String> chatFlux(@RequestParam(defaultValue = "") String prompt, String chatId) {
        // 保存会话ID，供前端历史列表使用
        chatHistoryRepository.save("chat", chatId);

        return chatClientFlux.prompt(prompt)
                // 1.1.x 常量迁移到 ChatMemory.CONVERSATION_ID（值不变：chat_memory_conversation_id）
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .chatResponse()
                .mapNotNull(this::extractContent);
    }

    /**
     * 从单个流式分片中抽取要下发的文本：优先思考内容，其次正式回答。
     * <p>
     * 关键点（M6 时代的两个坑）：
     * <ol>
     *   <li>thinking 的 key 是 {@code "thinking"}，不是 {@code "reasoningContent"}；
     *       且只存在于 1.1.x 及以上版本。</li>
     *   <li>取值类型必须是 {@code String}：{@code ChatGenerationMetadata.get} 是泛型方法
     *       {@code <T> T get(String)}，写成 AssistantMessage 会在运行期抛 ClassCastException。</li>
     * </ol>
     * Ollama 的思考与回答是 message 里的两个独立字段，会出现在不同的分片中，
     * 因此这里逐片透传，而不是"二选一返回"，否则正式回答会丢失。
     */
    private String extractContent(ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getResult() == null) {
            return null;
        }

        String thinking = chatResponse.getResult().getMetadata().get("thinking");
        if (thinking != null && !thinking.isBlank()) {
            return "<think>" + thinking + "</think>";
        }

        String text = chatResponse.getResult().getOutput().getText();
        return (text != null && !text.isBlank()) ? text : null;
    }

}
