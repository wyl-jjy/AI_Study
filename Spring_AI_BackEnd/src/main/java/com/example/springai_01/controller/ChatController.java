package com.example.springai_01.controller;

import com.example.springai_01.Interface.ChatHistoryRepository;
import com.example.springai_01.config.CommanConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@CrossOrigin("*")
@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    @Qualifier("chatClient")
    private ChatClient chatClient;
    @Autowired
    @Qualifier("chatClientFlux")
    private  ChatClient chatClientFlux;
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @RequestMapping("/chat")
    public String chat(@RequestParam(defaultValue = "讲个笑话")String prompt){
        String content = chatClient.prompt(prompt)
                .call()
                .content();
        return content;
    }
    @RequestMapping(path = "/chat-flux",produces = "text/html;charset=UTF-8")
    public Flux<String> chat_flux(@RequestParam String prompt,String chatId){
        //保存会话ID
        chatHistoryRepository.save("chat",chatId);

        //请求模型
        Flux<String> content = chatClientFlux
                .prompt(prompt)
                .advisors(a->a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();
        return content;
    }


}
