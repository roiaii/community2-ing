package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)  //只去扫描带有Controller注解的组件
public class ExceptionAdvice {

    //SpringBoot很强大 不需要在Controller上进行处理 只需要在这里进行统一处理

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})//该注解修饰方法，在controller出现异常后被调用，用于处理捕获到的异常
                                        // 表示处理所有异常 Exception所有异常的弗父类
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {  //有可能会用到请求和回复 注入进来
        logger.error("服务器发生异常: " + e.getMessage());    //将异常记录到日志当中
        for (StackTraceElement element : e.getStackTrace()) {   //遍历异常栈的信息 更详细的错误信息
            logger.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");   //通过请求方式来判断
        if ("XMLHttpRequest".equals(xRequestedWith)) {         //要判断是普通请求还是异步请求 普通请求返回505.html 异步请求返回JSON数据
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常!"));  //异步请求 响应字符串
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
