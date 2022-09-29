package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 查询用户信息
     * @return 用户对象
     */
    @GetMapping("/test")//localhost:8080/test
    @ResponseBody
    public User selectUser(){
        return userService.selectUser();
    }

    /**
     * 注册用户
     * @param username
     * @param password
     * @return 用户注册成功的code,message,data
     * @throws ImoocMallException
     */
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("username") String username,//@RequestParam：将请求参数绑定到你控制器的方法参数上（是springmvc中接收普通参数的注解）
                                    @RequestParam("password") String password) throws ImoocMallException {
        if(StringUtils.isEmpty(username)){//判断string类型的username是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)){//判断string类型的password是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        if(password.length()<8){//密码长度不能小于8位
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(username,password);
        return ApiRestResponse.success();
    }

    /**
     * 普通用户登录
     * @param username
     * @param password
     * @param session 保存user信息到session
     * @return 用户登录状态的code,message,data(User信息)
     * @throws ImoocMallException
     */
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 HttpSession session) throws ImoocMallException {
        if(StringUtils.isEmpty(username)){//判断username是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)){//判断password是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username,password);
        //保存用户信息时，不保存密码
        user.setPassword(null);
        //保存user信息到session
        session.setAttribute(Constant.IMOOC_MALL_USER,user);//key=Constant.IMOOC_MALL_USER, value=user
        //同时返回user信息
        return ApiRestResponse.success(user);
    }


    /**
     * 更改用户个性签名
     * @param session 从session中提取数据
     * @param personalizedSignature
     * @return
     * @throws ImoocMallException
     */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session,
                                          @RequestParam("personalizedSignature") String personalizedSignature) throws ImoocMallException {
        //提取session中的user对象（Constant.IMOOC_MALL_USER）
        User currentUser = (User)session.getAttribute(Constant.IMOOC_MALL_USER);
        //如果当前用户为空，提醒我们需要登录
        if(currentUser == null){
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        //更新数据
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(personalizedSignature);
        //调用更改个性签名方法
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    /**
     * 用户注销，清除session；注销的时候user和admin都会被注销
     * @param session
     * @return
     */
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session){
        //系统自带的removeAttribute方法
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    /**
     * 管理员登录接口
     * @param username
     * @param password
     * @param session 管理员用户存入session
     * @return
     * @throws ImoocMallException
     */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      HttpSession session) throws ImoocMallException {
        if(StringUtils.isEmpty(username)){//判断username是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)){//判断password是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        //先登录
        User user = userService.login(username,password);
        //登录以后，校验是否是管理员
        if (userService.checkAdminRole(user)) {//是管理员（role=2）返回true，执行操作
            //保存用户信息到session时，不保存密码
            user.setPassword(null);
            session.setAttribute(Constant.IMOOC_MALL_USER,user);//key=Constant.IMOOC_MALL_USER,value=user
            return ApiRestResponse.success(user);
        }else{
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }
}
