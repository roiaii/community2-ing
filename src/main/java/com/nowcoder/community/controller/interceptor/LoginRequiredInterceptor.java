package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {  //判断拦截的类型是否是方法因为也有可能是静态资源
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();  //获取到拦截的method对象
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);  //取该方法对象上指定的注解
            if (loginRequired != null && hostHolder.getUser() == null) {  //其中获取到的注解不为空 说明需要登录才能访问，但是请求中又没有持有用户
                response.sendRedirect(request.getContextPath() + "/login");//强制其重定向
                return false;  //该方法就表示 客户端访问的方法需要登录， 但是你又没登录
            }
        }
        return true;
    }
}
