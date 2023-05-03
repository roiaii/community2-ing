package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {

    private String topic; //消息类型
    private int userId; //消息是谁发出的
    private int entityType; //事件发生在哪个实体类型
    private int entityId;
    private int entityUserId; //实体的作者，实体属于谁
    private Map<String, Object> data = new HashMap<>(); //让业务具有扩展性

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {  //修改处理，set后返回当前事件对象，能连续调用set设置属性
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

}
