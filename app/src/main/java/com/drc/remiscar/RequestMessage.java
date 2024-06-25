package com.drc.remiscar;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class RequestMessage {
    /**
     * 租户编码
     */
    private String merCode;

    /**
     * 消息标识
     */
    private String flag;

    /**
     * 发送用户id
     */
    private String userId;
    /**
     * 发送用户
     */
    private String fromUser;

    /**
     * 接受用户
     */
    private List<String> toUser;

    /**
     * 发送时间
     */
    private Date sendDate;

    /**
     * 消息内容
     */
    private String message;

}
