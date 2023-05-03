package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@RunWith(SpringRunner.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Test
    public void testMail(){
        mailClient.sendMail("1127795343@qq.com", "Test", "Welcome.");
        //在这里激活邮件发送的是文本，而不是html，如果发送html需要构建content
    }
}
