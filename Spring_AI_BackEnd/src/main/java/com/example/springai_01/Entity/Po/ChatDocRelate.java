package com.example.springai_01.Entity.Po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2026-09-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_doc_relate")
@Builder
public class ChatDocRelate implements Serializable {

    private static final long serialVersionUID = 1L;

    private String chatId;

    @TableId(value = "doc_name")
    private String docName;


}
