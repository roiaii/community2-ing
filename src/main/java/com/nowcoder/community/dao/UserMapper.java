package com.nowcoder.community.dao;


import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


//将该接口注入容器当中，在mybatis中只需要写接口，就会自动生成实现类
//在这里还需要提供每个方法所需要的SQL语句，也就是要编写配置文件
@Repository  //在这里发现用Mapper注解并不能把该类注入到容器当中，原因未知，必须要加上Repository注解才行
@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user); //插入用户，返回影响了几行数据

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);









}
