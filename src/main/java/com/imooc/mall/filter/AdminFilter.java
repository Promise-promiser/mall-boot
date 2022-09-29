package com.imooc.mall.filter;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 管理员校验
 */
public class AdminFilter implements Filter {

    @Autowired
    UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = request.getSession();
        User currentUser = (User)session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null){
//            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN); 返回的类型为void，所以会报错，我们用其他方法来实现这个
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse)servletResponse).getWriter();
            out.write("{\n"
                    + "    \"status\": 10007,\n"
                    + "    \"msg\": \"NEED_LOGIN\",\n"
                    + "    \"data\": null\n"
                    + "}");//用户未登录
            out.flush();
            out.close();
            return;//执行到这里就结束了，不会进入过滤器调用和controller层
        }
        //2.检查是否是管理员
        boolean adminRole = userService.checkAdminRole(currentUser);
        if(adminRole){
            //如果是管理员放行
            filterChain.doFilter(servletRequest,servletResponse);
        }else{
            //不是管理员，提醒需要登录管理员
//            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN); 返回的类型为void，所以会报错，我们用其他方法来实现这个
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse)servletResponse).getWriter();
            out.write("{\n"
                    + "    \"status\": 10009,\n"
                    + "    \"msg\": \"NEED_ADMIN\",\n"
                    + "    \"data\": null\n"
                    + "}");//管理员未登录
            out.flush();
            out.close();
        }
    }


    @Override
    public void destroy() {

    }
}
