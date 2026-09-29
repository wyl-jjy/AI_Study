package com.example.springai_01.Entity.Po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@TableName("user_chat_relate")
public class UserChatRelate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "chat_Id", type = IdType.AUTO)
    private String chatId;

    private String userId;


}
