package com.nowcoder.community;

import com.nowcoder.community.controller.interceptor.AlphaInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class InterceptorTests {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Test
    public void testAlphaInterceptor(){

    }
}
