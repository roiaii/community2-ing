package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;


//对RedisTemplate进行配置，主要是配置序列化方式
@Configuration  //说明是一个配置类
public class RedisConfig {   //redis配置类

    @Bean  //定义第三方的bean  、、主要是为了使用第三方的类，使其能够被IOC容器管理，被自动注入
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        //返回什么类型，使用泛型  参数里面声明连接工厂，用来连接数据库，spring会自动将参数的bean注入进来
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //主要是配置序列化方式，也就是数据转换方式，将数据转化为什么形式
        // 设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());//返回一个能够序列化为字符串的序列化器
        // 设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());//转化为json形式，json是结构化的，也方便转换回来
        // 设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet(); //使 配置 生效
        return template;
    }

}
