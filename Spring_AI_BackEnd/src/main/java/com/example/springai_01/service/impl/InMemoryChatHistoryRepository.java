package com.example.springai_01.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.springai_01.Entity.Po.ChatTypeRelate;
import com.example.springai_01.Entity.Vo.Result;
import com.example.springai_01.mapper.ChatTypeRelateMapper;
import com.example.springai_01.service.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryChatHistoryRepository implements ChatHistoryRepository {

    private final Map<String, List<String>> chatHistory;

    //持久化到mysql(chatId-type)
    private final ChatTypeRelateMapper chatTypeRelateMapper;


    @Override
    public void saveByJDBC(String type, String chatId) throws Exception {
        if(type.isEmpty()||chatId.isEmpty()){
            throw new Exception("type||chatId 存在空值");
        }
        int insertResult = chatTypeRelateMapper.insert(ChatTypeRelate
                .builder()
                .chatId(chatId)
                .type(type)
                .build());
        if (insertResult == 0) {
            throw new Exception("会话历史存入失败");
        }
    }

    @Override
    public List<String> getChatIdsByJDBC(String type) {

//        QueryWrapper<ChatTypeRelate> selectByType = new QueryWrapper<>();
//        selectByType.eq("type", type);
//        List<ChatTypeRelate> list = chatTypeRelateMapper.selectList(selectByType);
         List<String> chatIds = chatTypeRelateMapper.selectByType(type);
         return chatIds==null ?List.of():chatIds;
    }

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