package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Aop 面向切面编程，是一种编程思想，是对OOP的一种补充。将业务功能和系统功能（记录日志、权限）进行解耦，提高程序的可维护性、可扩展性
 * 一些基本术语：
 * @Aspect切面，被封装成一个系统功能，能被Spring容器识别
 * @Joinpoint连接点，是程序执行中的某一个阶段点，比如方法的调用、异常的抛出。
 * @Pointcut切入点，是指切面（系统功能）和程序的交叉点，要么是类名或者方法名。
 * @Advice通知，增强处理，是切面类中的方法，是切面的具体实现
 * 代理：将通知应用到目标对象上之后，动态创建的对象。可以作为目标对象使用。
 * 还有一些通知的类型：比如前置通知（在方法执行之前）、后置通知、环绕通知、异常通知（在抛出异常时通知）
 * 以上都可以采用注解实现，免去配置的复杂性。
 *
 *
 *
 * 实现AOP的方式：
 * 1、Spring AOP : 纯java实现的，不需要专门的百衲衣过程和类加载器，
 *      在运行期间通过代理的方式向目标对象植入增强的代码。
 *  在这里，实现动态代理的方式有两种，
 *  jdk动态代理：需要被代理的对象实现某个接口
 *  CGlib代理：对被代理对象生成子类，并对子类进行增强。基于继承实现的。
 *
 * 2、AspectJ ：扩展了java语言，需要专门的编译器，在编译时增强。
 */


//@Component
//@Aspect   //声明为切面组件
public class AlphaAspect {

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")   //注解描述 第一个*表示所有的返回类型 //service包下所有的类所有的方法所有的参数 都作为且切点
    public void pointcut() {  //声明切点

    }

    @Before("pointcut()")  //在切点之前织入代码 并描述以谁为切点
    public void before() {
        System.out.println("before");
    }

    @After("pointcut()")  //在切点之后织入代码
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()")  //在返回值之后织入代码
    public void afterRetuning() {
        System.out.println("afterRetuning");
    }

    @AfterThrowing("pointcut()")  //在抛异常时织入代码
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Around("pointcut()")  //既想在之前又想在之后织入代码
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object obj = joinPoint.proceed();  //获取目标组件的方法
        System.out.println("around after");
        return obj;
    }

}
