package com.imooc.mall.service.impl;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.UserMapper;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import com.imooc.mall.utils.MD5Utils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 查询用户信息
     * @return 用户对象
     */
    @Override
    public User selectUser() {
        return userMapper.selectByPrimaryKey(1);//通过主键找到用户对象，即找到主键为1的用户对象
    }

    /**
     * 注册用户
     * @param username
     * @param password
     * @throws ImoocMallException
     */
    @Override
    public void register(String username, String password) throws ImoocMallException {
        //1.检验是否重名
        //通过用户名查找用户
        User result = userMapper.selectByName(username);
        //如果有重复的用户名,抛出异常
        if(result != null){
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        //赋值
        User user = new User();
        user.setUsername(username);
        user.setRole(1);
        user.setCreateTime(new Date());
        try {
            //向数据库存加盐以后的密码
            user.setPassword(MD5Utils.getMD5Str(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //插入数据库，返回的是往数据库里面插入的数据数量
        int count = userMapper.insertSelective(user);
        if(count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public User login(String username, String password) throws ImoocMallException {
        String md5Password = null;
        try {
            //将输入的密码，转为md5形式
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //检验用户名密码与数据库是否匹配
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    /**
     * 更改用户个性签名
     * @param user
     * @throws ImoocMallException
     */
    @Override
    public void updateInformation(User user ) throws ImoocMallException {
        //更改个性签名
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        //如果更新的数据大于1，则数据库更新失败（ByPrimaryKey主键不能重复）
        if(updateCount > 1 ){
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 判断用户是普通用户还是管理员用户
     * @param user 对象
     * @return true 2/false 1
     */
    @Override
    public boolean checkAdminRole(User user){
        //1是普通用户，2是管理员用户
        return user.getRole().equals(2);
    }
}
