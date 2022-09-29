package com.imooc.mall.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 在Java控制台打印请求和响应信息；使用AOP拦截器
 */
@Aspect//AOP;作用是把当前类标识为一个切面供容器读取
@Component//组件注解,通用注解,被该注解描述的类将被IoC容器管理并实例化
public class WebLogAspect {

    //记录日志
    private final Logger log = LoggerFactory.getLogger(WebLogAspect.class);

    //对此方法执行前后进行拦截
    @Pointcut("execution(public * com.imooc.mall.controller.*.*(..))")//拦截点
    public void webLog(){
    }

    //对拦截点之前的拦截
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){//JoinPoint保存的是方法的信息
        //收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();//获取当前请求
        log.info("URL:"+request.getRequestURL().toString());
        log.info("HTTP_METHOD:"+request.getMethod());
        log.info("IP:"+request.getRemoteAddr());
        //joinPoint.getSignature().getDeclaringTypeName()获取类的信息
        log.info("ClASS_METHOD:"+joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getDeclaringType());
        log.info("ARGS:"+ Arrays.toString(joinPoint.getArgs()));
    }

    //对拦截点之后的拦截
    @AfterReturning(returning = "res",pointcut = "webLog()")
    public void doAfterReturning(Object res) throws JsonProcessingException {
        //处理完请求，返回内容
        log.info("RESPONSE:"+new ObjectMapper().writeValueAsString(res));
    }

}
