package com.nowcoder.community.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;




@Configuration  //配置类注解，通常带有几个Bean来将返回的类注入到容器中，代替传统的xml配置方式
public class AlphaConfig {

    @Bean  //该方法返回的对象将被装配到容器中；并且Bean的名字为方法名；  将第三方的包中的类装配到容器中
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    }
}
