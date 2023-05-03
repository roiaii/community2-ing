package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);//记录日志的组件

    @Autowired
    private MessageService messageService;


    public void setThreadPoolExecutor(ConsumerRecord record){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5,
                1000, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                handleCommentMessage(record);
            }
        };
        threadPoolExecutor.submit(task);
    }




    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW}) //一个方法消费多个主题 多对多关系
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);//将JSON字符串解析为类型对象
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic()); //conversation内容改变，存topic类型
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();//Content存通知内容需要的条件，用map来存
        content.put("userId", event.getUserId()); //谁发的
        content.put("entityType", event.getEntityType()); //实体类型 （对一个帖子既可以评论，也可以点赞）
        content.put("entityId", event.getEntityId());    //对一个评论既可以评论，也可以点赞
                                                //entityType记录的就是实体类型（目标类型）
                                                //topic记录的就是消息类型
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue()); //把其他数据一股脑儿存到content中
            }
        }

        message.setContent(JSONObject.toJSONString(content)); //将content转化为JSON字符串
        messageService.addMessage(message); //存数据库
    }
}
