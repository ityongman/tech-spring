package com.ityongman.protocal;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author shedunze
 * @Date 2020-03-02 15:34
 * @Description 用于解析java数据的自定义传输协议, 其实是存储关键信息的 Bean
 */
@Getter
@Setter
public class InvokerProtocol implements Serializable {
    /**
     * 需要调用的类名
     */
    private String className ;

    /**
     * 方法名
     */
    private String method ;

    /**
     * 参数类型
     */
    private Class<?>[] parames ;

    /**
     * 参数对应的值
     */
    private Object[] values ;
}
