package com.nowcoder.community.dao;


import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
public class AlphaDaoImpl2 implements AlphaDao {
    @Override
    public String select() {
        return "我是AlphaDao的第二个实现类！";
    }
}
