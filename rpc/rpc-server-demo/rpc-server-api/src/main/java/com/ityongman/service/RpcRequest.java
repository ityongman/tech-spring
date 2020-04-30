package com.ityongman.service;

import java.io.Serializable;

/**
 * @Author shedunze
 * @Date 2020-02-02 13:38
 * @Description
 */
public class RpcRequest implements Serializable {
    /**
     * 处理的对象
     */
    private String className ;
    /**
     * 处理的对象的方法
     */
    private String methodName ;
    /**
     * 参数类型
     */
    private Class<?>[] paramTypes ;
    /**
     * 处理方法的参数
     */
    private Object[] args ;
    /**
     * 版本信息
     */
    private String version ;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
