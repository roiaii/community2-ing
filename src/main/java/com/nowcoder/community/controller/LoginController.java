package com.nowcoder.community.controller;


import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        //对于该方法的参数做说明
        //user是在响应请求调用该方法时就自动根据前端表单数据基于同名一致原则将值填入到user类中
        //model参数也是在调用该方法时，由容器自动初始化等
        //上述操作由spring处理，不需要我们管 查看源码了解细节
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()) {//成功注册
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");//向模板添加自动跳转首页的参数
            return "/site/operate-result";//返回模板的路径
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";//注册失败的话， 返回原来的注册页面
        }
    }
    //激活邮件
    //http://localhost:8083/community///http://localhost:8083/community/activation/用户id/激活码用户id/激活码
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        //使用model为将要使用的模板传参
        int result = userService.activation(userId, code);
        if(result == ACTIVATION_SUCCESS){//激活成功
            model.addAttribute("msg", "激活成功，你的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");//向模板添加自动跳转首页的参数
        } else if(result == ACTIVATION_REPEAT){//重复激活
            model.addAttribute("msg", "无效操作，你的账号已激活！");
            model.addAttribute("target", "/index");//向模板添加自动跳转首页的参数
        } else{//激活失败
            model.addAttribute("msg", "激活失败，你提供的激活码无效！");
            model.addAttribute("target", "/index");//向模板添加自动跳转首页的参数
        }
        return "/site/operate-result";//这里面逻辑可太多了，在该模板里面包含跳转链接，而跳转链接就是我们写的html网页
    }

    //获取验证码
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);//利用封装的工具

        // 将验证码存入session
        //session.setAttribute("kaptcha", text);

        //优化验证码
        // 验证码的归属  //将临时登录凭证放入cookie 发送给客户端
        String kaptchaOwner = CommunityUtil.generateUUID(); //临时登录凭证
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);//存入redis，过期时间60s



        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败:" + e.getMessage());
        }
    }

    //登录功能
    @RequestMapping(path = "/login" , method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, /*HttpSession session,*/ HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        //在这里说明一个默认的规则，如果参数为实体类，是直接放在model中的，可以有前端模板直接获取
        //如果说参数是普通的基本类型，一种是在方法体中放入model中，另一种是因为该请求还没有结束，可以在
        //前端模板中通过语法获取

        //登录页面生成的验证码存入了session中，所以要声明，需要将生成的凭证放到cookie中，所以声明response


        //检查验证码
        //之前验证码是放入session，现在不用从Session里取
        //String kaptcha = (String) session.getAttribute("kaptcha");

        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {//这个cookie也有有效时间，需要判断是否存在
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }


        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            //session中验证码为空、用户输入验证码为空、两者不同时，将提示传给前端模板
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/login";
        }
        //检查账号、密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;//判断凭证有效时间
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){//登录成功
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());//将凭证放入创建的cookie中，cookie就是字符串，key-value形式
            cookie.setPath(contextPath);//设置cookie的有效路径为整个项目
            cookie.setMaxAge(expiredSeconds);//设置cookie有效时间
            response.addCookie(cookie);//将cookie添加到响应当中，在响应时返回给客户端
            return "redirect:/index";
        } else{//登录失败
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    //退出功能
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }



}
