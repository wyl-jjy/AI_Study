package com.example.springai_01.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springai_01.Entity.Po.ChatTypeRelate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChatTypeRelateMapper extends BaseMapper<ChatTypeRelate>{

    @Select("select chat_id from chat_type_relate where type=#{type} ")
    List<String> selectByType(@Param("type") String type);
}
