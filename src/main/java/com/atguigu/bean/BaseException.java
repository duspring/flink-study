package com.atguigu.bean;

/**
 * 异常基类
 * @Auther: spring du
 * @Date: 2021/1/5 14:30
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
