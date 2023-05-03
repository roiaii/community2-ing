package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() {
        kafkaProducer.sendMessage("test", "你好");
        kafkaProducer.sendMessage("test", "work hard");

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

@Component
class KafkaProducer { //生产者

    @Autowired
    private KafkaTemplate kafkaTemplate; //已经被spring整合，在容器中，直接注入进来使用

   public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);  //为哪个话题发什么消息
    }

}

@Component
class KafkaConsumer { //消费者

    @KafkaListener(topics = {"test"})  //注解，监听哪个主题，服务启动后，spring会阻塞在那里去读消息，
    public void handleMessage(ConsumerRecord record) {// 没有消息就阻塞，有消息读取给到下面这个方法
        //该方法将消息封装为ConsumerRecord
        System.out.println(record.value());
    }


}