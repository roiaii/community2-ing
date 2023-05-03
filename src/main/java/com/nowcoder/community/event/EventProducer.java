package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 处理事件（业务角度） 发布消息（技术角度）
    public void fireEvent(Event event) { //事件传进来
        // 将事件发布到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));//将事件中所有数据转化为JSON字符串
                                                                            //消费者拿到数据在转化为事件对象
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
        //future.addCallback(result -> log.info("发送消息到topic：{} partition；{}"));  //添加回调函数
        //怎样保证消息不丢失
        //保证生产者方消息不丢失
    }



}
