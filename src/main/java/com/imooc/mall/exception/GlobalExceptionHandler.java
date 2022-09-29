package com.imooc.mall.exception;

import com.imooc.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一异常的handler（处理者）
 */
@ControllerAdvice//拦截两种异常：系统错误Exception|业务异常ImoocMallException
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    //系统错误异常
    @ExceptionHandler(Exception.class)//拦截的异常为Exception
    @ResponseBody
    public Object handleException(Exception e){
        log.error("Default Exception:",e);
        //把系统错误异常拦截，然后在这里定义如何输出
        return ApiRestResponse.error(ImoocMallExceptionEnum.SYSTEM_ERROR);
    }


    //业务异常
    @ExceptionHandler(ImoocMallException.class)//拦截的异常为ImoocMallException
    @ResponseBody
    public Object handleImoocMallException(ImoocMallException e){
        log.error("Default ImoocMallException:",e);
        return ApiRestResponse.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)//拦截的异常为ImoocMallException
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("Default MethodArgumentNotValidException:",e);//打印出日志
        return handleBindingResult(e.getBindingResult());
    }

    //一个方法
    private ApiRestResponse handleBindingResult(BindingResult result){//BindingResult用在实体类校验信息返回结果绑定。
        //把异常处理为对外的提示
        List<String> list = new ArrayList<>();
        if(result.hasErrors()){
            List<ObjectError> allErrors = result.getAllErrors();
            for (int i = 0; i<allErrors.size(); i++){
                ObjectError objectError = allErrors.get(i);
                String message = objectError.getDefaultMessage();
                list.add(message);
            }
        }
        if(list.size() == 0){//返回参数异常
            return ApiRestResponse.error(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(),list.toString());//智能化提示异常
    }
}
