package com.nowcoder.community;

import com.nowcoder.community.config.AlphaConfig;
import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@RunWith(SpringRunner.class)
public class CommunityApplicationTests implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override   //通过重写setApplicationContext方法 获取容器对象 所以是自动创建了容器， 我们并没有去new容器对象
        public void setApplicationContext(ApplicationContext applicationContext)throws BeansException {
            this.applicationContext = applicationContext;
        }

    @Test
    public void contextLoads() {
      //  System.out.println("hello world"+applicationContext);

        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);//控制反转的体现
        System.out.println(alphaDao.select());//从容器中直接获取想要的bean
    }

    @Test
    public void testBeanManagement(){
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }

    @Test
    public void testBeanConfig(){
    //    AlphaConfig alphaConfig = applicationContext.getBean(AlphaConfig.class);
      //  System.out.println("Hello! "+alphaConfig.simpleDateFormat());

        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    @Autowired//注明该类为容器的属性，也就是说将依赖注入  //注意理解这句话，以及Autowired注解的作用
    @Qualifier("alphaDaoImpl2")  //使用该注解，当某个接口有多个实现类的时候，使用还注解指定将哪个类
    //注入到容器当中，不然的话就默认注入primary类  //在这里注明使用第二个实现类
    private AlphaDao alphaDao;
    @Autowired    //注入注解 加的位置有三种：可以是属性前，可以是构造器方法前，也可以是set方法前
    private AlphaService alphaService;
    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Test
    public void testDI(){
        System.out.println(alphaDao.select());
        System.out.println(alphaService);
        System.out.println(simpleDateFormat);


    }

}
