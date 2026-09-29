package com.example.springai_01.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.springai_01.Entity.Po.Messagehistory;
import com.example.springai_01.mapper.MessagehistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMemoryJDBCRepository implements ChatMemory {


    private final MessagehistoryMapper messagehistoryMapper;


    @Override
    public void add(String conversationId, List<Message> messages) {
        Messagehistory messagehistory = new Messagehistory();
        for (Message message : messages) {
            boolean res = messagehistoryMapper.insertOrUpdate(messagehistory
                    .setChatId(conversationId)
                    .setCreateTime(LocalDateTime.now())
                    .setMessageType(String.valueOf(message.getMessageType()))
                    .setMessage(message.getText())
            );
            if(!res){
                log.debug("插入会话记忆失败");
            }
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        List<Messagehistory> messagehistories = messagehistoryMapper.selectList(
                Wrappers.<Messagehistory>lambdaQuery()
                        .eq(Messagehistory::getChatId, conversationId)
                        .orderByAsc(Messagehistory::getCreateTime)
        );
        return messagehistories.stream()
                .map(this::toMessage)
                .collect(Collectors.toList());

    }

    private Message toMessage(Messagehistory messagehistory) {
        String type = messagehistory.getMessageType();
        String content = messagehistory.getMessage();
        return switch (type) {
            case "USER"      -> new UserMessage(content);
            case "ASSISTANT" -> new AssistantMessage(content);
            case "SYSTEM"    -> new SystemMessage(content);
            default -> throw new IllegalArgumentException("未知消息类型: " + type);
        };
    }

    @Override
    public void clear(String conversationId) {

    }
}
