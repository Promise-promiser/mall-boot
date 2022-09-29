package com.imooc.mall.service;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.pojo.User;

public interface UserService {

    /**
     * 查询用户信息
     * @return 用户对象
     */
    User selectUser();

    /**
     * 注册用户
     * @param username
     * @param password
     * @throws ImoocMallException
     */
    void register(String username,String password) throws ImoocMallException;

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password) throws ImoocMallException;

    /**
     * 更改用户个性签名
     * @param user
     * @throws ImoocMallException
     */
    void updateInformation(User user) throws ImoocMallException;

    /**
     * 判断用户是普通用户还是管理员用户
     * @param user
     * @return
     */
    boolean checkAdminRole(User user);
}
