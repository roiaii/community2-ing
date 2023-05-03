package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@Deprecated  //该组件不推荐使用。用redis存
public interface LoginTicketMapper {
    //不使用配置文件，通过注解来最数据库中的表做增删改查
    //注解的方式也支持动态SQL
    @Insert({"insert into login_ticket (user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")//指明插入数据时自动生成主键，并且回传给实体类中的"id"属性
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({"select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);//隐式的指明接口中的方法为 public abstract

    @Update({"update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket, int status);

}
