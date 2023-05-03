package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    //@Autowired
    //private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public Map<String, Object> register(User user){//在这里说明一下user参数怎么来的
        //首先是注册页面通过post方式提交表单 spring根据同名一致原则将其封装成user对象放在controller层 然后传递给Service层
        //然后我们根据前端表单传递过来的数据进行处理
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        //验证账号
        User u = userMapper.selectByName(user.getUsername());//根据用户名从数据库查到 已经存在一个user
        if(u != null){
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());//根据邮箱从数据库中查到 已经存在一个user
        if(u != null){
            map.put("emailMsg", "该邮箱已存在！");
            return map;
        }
        //注册用户         //其实也是在完善这个user对象 比如说密码加密 用户类型 状态 激活码 用户头像 创建日期 最后再向数据库增加一条记录
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));//通过md5算法加密
        user.setType(0);//普通用户                                              //md5算法对原始密码加随机字符串组合的新字符串进行加密
        user.setStatus(0);//未激活
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt()));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);//在这里数据库中增加一条用户记录，mybatis会自动生成用户id并回传给实体类
        //字段全部处理完毕
        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //http://localhost:8083/community/activation/用户id/激活码
        String url =domain + contextPath + "/activation" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }
    //需要去理解的几个逻辑，发送邮件的逻辑、使用模板以及后端参数去拼接合成html文件
    //spring功能还是挺强大的

    //激活账号
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){//重复激活
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)){//激活成功
            userMapper.updateStatus(userId, 1);
            clearCache(userId);//修改了user信息，需要删除缓存
            return ACTIVATION_SUCCESS;
        }
        else{//激活失败
            return ACTIVATION_FAILURE;
        }
    }

    //登录功能
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        //在这里比对密码时，通过将前端传过来的明文密码通过md5算法加密，与数据库中密码进行比对
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        //账号验证
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        //验证状态
        if(user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码错误！");
            return map;
        }
        //至此，登录成功，生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());//生成随机字符串  login_ticket内容是生成一段随机字符串
        loginTicket.setStatus(0);//设置为有效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        //凭证有效时间为expiredSeconds //值是controller层传过来的

        //优化
        //loginTicketMapper.insertLoginTicket(loginTicket);//存进数据库
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);//存入redis，值是loginTicket对象，当字符串存，redis将对象序列化为字符串
        //将登陆成功信息传给map //service与controller层通过map来进行交互
        map.put("ticket", loginTicket.getTicket());
        return map;
    }
    //退出功能
    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        //取出来，修改，再传进去
    }

    public LoginTicket findLoginTicket(String ticket) {  //拦截器调用该方法
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        //return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);//先更新再删除缓存，因为无法对数据库操作、redis操作放到一个事务里。
                            //先删除缓存的话，如果更新失败的话，缓存也没有了，所以先更新mysql，再删除缓存
        return rows;
    }

    public User findUserById(int id){ //拦截器中频繁调用该方法
        //return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }


    /*
    做缓存，分这几步：
    1、2、3
     */

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);//过期时间3600s
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey); //这里删除缓存有可能失败，从而导致数据一致性问题
    }




    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }


    //修改密码
    public int updatePassword(User user, String password) {
        if(StringUtils.isBlank(password)){
            return -1;
        }

        return userMapper.updatePassword(user.getId(), password);
    }

}
