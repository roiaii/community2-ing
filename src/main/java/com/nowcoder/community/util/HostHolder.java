package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息,用于代替session对象.
 */

//将通过ticket获取到的user放到该对象当中，该对象为相应线程对应的对象，使用ThreadLocal实现线程隔离
    //ThreadLocal说明：线程变量，以ThreadLocal对象为key，任意对象为值的存储结构，该结构被附带在线程上
    //set()设置这个值 get()获取这个值
    //所以在这里，key为ThreadLocal对象users，值为user对象
    //可以将一个值绑定到该线程上
@Component
public class HostHolder {  //就是对ThreadLocal进行了封装，用来容纳user变量
                            //HostHolder作用就是将变量user绑定到线程当中、从线程中获取变量、将线程中变量移除

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
