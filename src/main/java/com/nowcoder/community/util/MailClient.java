package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;//所以说实现发送邮件功能就很方便
                                        //java中已经提供相应的类来实现功能

    @Value("${spring.mail.username}")
    private String from;

    //发送给谁，标题是啥， 内容是啥
    public void sendMail(String to, String subject, String content ){

        try {
            MimeMessage message = mailSender.createMimeMessage();
            //Helper类帮助我们构建要发送的邮件
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            logger.error("发送邮件失败：" + e.getMessage());
        }


    }





}
