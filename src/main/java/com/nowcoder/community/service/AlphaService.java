package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
public class AlphaService {

    @Autowired
    @Qualifier("alphaDaoImpl2")
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;//注入Spring提供的Bean 实现函数式事务


    public AlphaService(){
        System.out.println("我是构造器！");
    }

    @PostConstruct  //用来初始化某些数据，在构造器调用之后调用，用来初始化某些数据，该注解的功能。
    //需要用注解来声明，而构造器方法是不需要用注解声明
    public void init(){
        System.out.println("初始化...");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("我是销毁方法！");
    }

    public String find(){
        return alphaDao.select();
    }

    //事务管理demo 场景：注册新用户，并自动发新人帖子 这看作一整个事务
    //Spring提供对事务管理的支持，两类：
    //1、声明式事务 通过注解实现
    //2、函数式事务 eg：十个操作中只有两个操作需要包装成事务，就要用函数式事务，提高性能（事务是需要加锁的，加锁是消耗性能的）


    // REQUIRED: 支持当前事务(外部事务),如果不存在则创建新事务.  //当前事务就是外部事务 eg：A调用B事务 A就是当前事务（外部事务） 支持当前事务，就是以当前事务为标准
    // REQUIRES_NEW: 创建一个新事务,并且暂停当前事务(外部事务).
    // NESTED: 如果当前存在事务(外部事务),则嵌套在该事务中执行(有独立的提交和回滚),否则就会REQUIRED一样.
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    //这参数propagation是表示事务传播机制 是解决 当事务A调用事务B时 以谁为标准的问题 支持谁就以谁为标准
    //该注解声明该方法为事务（在里面造了个错误，此时会回滚）事务就是不可中断的一个或几个操作，要么都执行，要么都不执行

    //声明式事务
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道!");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");    //造的错误

        return "ok";
    }

    //函数式事务
    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好");
                post.setContent("我是新人!");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");  //造个错误

                return "ok";
            }
        });
    }
}
