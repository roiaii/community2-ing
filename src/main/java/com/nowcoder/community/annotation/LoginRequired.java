package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//自定义的注解，用来拦截在登录状态下才能访问的方法
//@Decument 用来指定 生成文档时是否要保留该注解
//@Inherited 指定子类继承父类时是否要继承该注解
@Target(ElementType.METHOD)  //指定该注解作用在哪里，类还是方法
@Retention(RetentionPolicy.RUNTIME)  //指定高注解作用时间，是编译时还是运行时
public @interface LoginRequired {

}
