package com.imooc.mall.common;

import com.imooc.mall.exception.ImoocMallExceptionEnum;

/**
 * 通用返回对象(统一返回对象)
 *
 * @param <T>
 */
public class ApiRestResponse<T> {
    private Integer status;
    private String msg;
    private T data;//泛型

    //定义两个常量
    private static final int OK_CODE = 10000;
    private static final String OK_MSG = "SUCCESS";

    //三个参数构造函数
    public ApiRestResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    //二个参数构造函数
    public ApiRestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    //默认信息，无参构造函数
    public ApiRestResponse() {
        this(OK_CODE, OK_MSG);
    }

    //成功的方法
    public static <T> ApiRestResponse<T> success() {
        return new ApiRestResponse<>();
    }

    //成功带data的方法
    public static <T> ApiRestResponse<T> success(T result) {
        ApiRestResponse<T> response = new ApiRestResponse<>();
        response.setData(result);
        return response;
    }

    //错误的方法；
    public static <T> ApiRestResponse<T> error(Integer code, String msg) {
        return new ApiRestResponse<>(code, msg);
    }

    //传入异常枚举的error方法（嵌套）；由此引出枚举类
    public static <T> ApiRestResponse<T> error(ImoocMallExceptionEnum ex) {
        return new ApiRestResponse<>(ex.getCode(), ex.getMsg());
    }

    @Override
    public String toString() {
        return "ApiRestResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static int getOkCode() {
        return OK_CODE;
    }

    public static String getOkMsg() {
        return OK_MSG;
    }
}
