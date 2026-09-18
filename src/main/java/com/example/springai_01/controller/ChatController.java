package com.example.springai_01.controller;

import com.example.springai_01.config.CommanConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    @Qualifier("chatClient")
    private ChatClient chatClient;
    @Autowired
    @Qualifier("chatClientFlux")
    private  ChatClient chatClientFlux;

    @RequestMapping("/chat")
    public String chat(@RequestParam(defaultValue = "讲个笑话")String prompt){
        String content = chatClient.prompt(prompt)
                .call()
                .content();
        return content;
    }
    @RequestMapping(path = "/chat-flux",produces = "text/html;charset=UTF-8")
    public Flux<String> chat_flux(@RequestParam String prompt){
        Flux<String> content = chatClientFlux.prompt(prompt).stream().content();
        return content;
    }


}
