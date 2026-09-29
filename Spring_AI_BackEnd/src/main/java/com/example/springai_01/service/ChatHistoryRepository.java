package com.example.springai_01.service;

import java.util.List;

public interface ChatHistoryRepository {

    void saveByJDBC(String type,String chatId) throws Exception;


    List<String> getChatIdsByJDBC(String type);


    /**
     * 保存会话记录
     * @param type 业务类型，如：chat、service、pdf
     * @param chatId 会话ID
     */
    void save(String type, String chatId);

    /**
     * 获取会话ID列表
     * @param type 业务类型，如：chat、service、pdf
     * @return 会话ID列表
     */
    List<String> getChatIds(String type);
}
