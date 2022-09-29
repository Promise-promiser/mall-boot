package com.imooc.mall.model.dao;

import com.imooc.mall.model.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /*通过用户名查找用户对象,检验是否重复登录用到*/
    User selectByName(String username);

    /*检验用户前台输入的用户名密码数据库中是否有对应的数据，有则返回user对象*/
    User selectLogin(@Param("username") String username, @Param("password") String password);
}