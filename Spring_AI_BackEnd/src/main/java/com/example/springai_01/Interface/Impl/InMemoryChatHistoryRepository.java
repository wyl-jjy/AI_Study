package com.example.springai_01.Interface.Impl;

import com.example.springai_01.Interface.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryChatHistoryRepository implements ChatHistoryRepository {

    private final Map<String, List<String>> chatHistory;

    @Override
    public void save(String type, String chatId) {
        //如果业务类型中不存在，先在map中创建
        if (!chatHistory.containsKey(type)) {
            chatHistory.put(type, new ArrayList<>());
        }
        List<String> chatIds = chatHistory.get(type);
//        List<String> chatIds = chatHistory.computeIfAbsent(type, k -> new ArrayList<>());
//        if (chatIds.contains(chatId)) {
//            return;
//        }
        if(chatIds.contains(chatId)){
            return;
        }
        chatIds.add(chatId);
    }

    @Override
    public List<String> getChatIds(String type) {
        /*List<String> chatIds = chatHistory.get(type);
        return chatIds == null ? List.of() : chatIds;*/
        return chatHistory.getOrDefault(type, List.of());
    }
}