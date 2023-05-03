package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller  //注明为控制器类，在spring扫描时会标注到，spring有它自己的扫描机制
@RequestMapping("/alpha")  //该注解用于映射一个请求或者映射一个方法 ， 用来处理相应的请求
//当标注在一个方法上时，该方法为请求处理方法，在程序接收到相应的URL时被调用
//如果标注在类上面，类中的所有方法都映射为相对应于类级别的请求
//在这里就标注在类上，所以访问路径需要先加上类的路径再加上类中方法的路径
public class AlphaController {

    @Autowired
    private AlphaService alphaService;
    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring boot.";
    }

    @RequestMapping("/https")   //在Controller中处理请求以及响应的方式，比较底层的方法
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取响应数据
        System.out.println(request.getMethod());  //输出该请求的方式是POST还是GET方式
        System.out.println(request.getServletPath());//输出该请求的路径
        Enumeration<String> enumeration = request.getHeaderNames();
            while(enumeration.hasMoreElements()){
                String name = enumeration.nextElement();
            String value = request.getHeader(name);

            System.out.println(name+" : "+value);
        }
        System.out.println(request.getParameter("code"));
        //返回响应数据
        response.setContentType("text/html; charset=utf-8");
        try (
                PrintWriter writer = response.getWriter();   //获取流
        ){
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //使用简便的方式处理请求和响应
    //"/students?curreent=0&limit&2
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    //解释几个注解的意思，@RequestMapping注解中path说明访问路径，method说明要处理的请求的请求方式；
    //@RequestParam注解用来说明当访问url中含有参数时怎么处理，一个是将请求中的参数赋值给方法参数
    //另一个是是否必须需要参数，最后是请求中没有参数时默认赋给方法参数什么值
    //主要是学习了怎样用注解处理请求中的参数，对比上面使用较为底层的处理请求的方法，该注解方式更为简便
    //在上述方法中是在处理请求体中，声明了请求以及响应对象参数，而这个参数是由spring默认给的，之后在
    //使用参数去获取各种请求中的参数以及返回响应数据
    public String getStudents(@RequestParam(name = "current", required = false, defaultValue = "1") int current,
                              @RequestParam(name = "limit", required = false, defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }
    //当参数在路径当中怎么使用注解去获取
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET) //注意这里是花括号
    //在这里URL为https://localhost:8083/community/alpha/student/999
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //使用Post请求方式
    //以下为使用post请求方式获取数据的处理方式
    //简言之就是，参数不在通过url传递到后端（因为长度有限并且数据容易暴露）在表单的话，html中设置的参数名
    //要和请求处理方法中的参数名一致，sping就会自动将表单提交参数赋值给请求处理方法中相对应的参数，至于怎样
    //实现的，需要阅读相应的spring源码，我们只需要用就行，目前为止
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, String age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应HTML数据   //向浏览器响应html数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    //在这里不使用@ResponseBody注解，默认就返回html，否则加上会返回JSON数据
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");  //在这里将数据写死
        mav.addObject("age", 23);
        mav.setViewName("/demo/view");
        return mav;
    }
    //第二种稍简洁的方法
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "北京大学校");
        model.addAttribute("age", 23);
        return "demo/view";
    }

    //使用json进行不同数据格式的交互
    //json它是一种字符串类型
    //响应json数据(异步请求）
    //java对象->json->js对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody  //加上该注解说明返回的是JSON格式，如果不加，默认返回html格式
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 80000.00);
        return emp;  //在这里返回map对象，注解ResponseBody会自动将对象转化为JSON字符串类型
    }

    //返回多个对象的情况
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new LinkedList();

        Map<java.lang.String, java.lang.Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 80000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 25);
        emp.put("salary", 80000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 26);
        emp.put("salary", 80000.00);
        list.add(emp);

        return list;  //在这里返回多个对象，并且存放在List数组中，同样注解会将其转化为JSON字符串形式
    }


    // cookie示例
    //在视图层就可以完成cookie示例，只是浏览器与服务器两者之间的通信
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie生效的范围
        cookie.setPath("/community/alpha");
        // 设置cookie的生存时间
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    // session示例

    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // ajax示例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "操作成功test!");
    }

}

