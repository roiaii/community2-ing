package com.nowcoder.community;


import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//import java.sql.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@RunWith(SpringRunner.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;


    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }
    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abcd");
        user.setHeaderUrl("www.nowcoder.com/101.PNG");
        user.setEmail("nowcoder101@sina.com");
        user.setCreateTime(new Date());

        int crows = userMapper.insertUser(user);
        System.out.println(crows);
    }

    @Test
    public void testUpdateUser(){
        int crows = userMapper.updateHeader(150,"www.lijun.com");
        System.out.println(crows);
        crows = userMapper.updatePassword(150, "zbcd");
        System.out.println(crows);
        crows = userMapper.updateStatus(150, 1);
        System.out.println(crows);

    }


    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for(DiscussPost post : list) {
            System.out.println(post);
        }
        //使用迭代器进行遍历
        Iterator<DiscussPost> iterator = list.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }
   /* @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0,10);
                for(DiscussPost post : list){
                System.out.println(post);
            }
                //使用迭代器进行遍历
        Iterator<DiscussPost> iterator = list.iterator();
                while(iterator.hasNext()){
                    System.out.println(iterator.next());
                }
      //          int rows = discussPostMapper.selectDiscussPostRows(0);
    //    System.out.println(rows);
    } */

   @Test
    public void testInsertLoginTicket(){
       LoginTicket loginTicket = new LoginTicket();
       loginTicket.setUserId(101);
       loginTicket.setStatus(0);
       loginTicket.setTicket("abc");
       loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 *10));

       loginTicketMapper.insertLoginTicket(loginTicket);
   }
   @Test
    public void testSelectLoginTicket(){
       LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
       System.out.println(loginTicket);

       //更新操作
       int rows = loginTicketMapper.updateStatus("abc", 1);
       System.out.println(rows);
       loginTicket = loginTicketMapper.selectByTicket("abc");
       System.out.println(loginTicket);
   }


}
