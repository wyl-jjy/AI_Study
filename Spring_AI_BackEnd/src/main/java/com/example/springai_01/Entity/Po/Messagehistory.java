package com.example.springai_01.Entity.Po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2026-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("messagehistory")
@NoArgsConstructor
public class Messagehistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "chat_Id")
    private String chatId;

    @TableField("message")
    private String message;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("message_type")
    private String messageType;

}
