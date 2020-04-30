package com.ityongman.spring.handler;

import com.ityongman.service.RpcRequest;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author shedunze
 * @Date 2020-03-10 15:17
 * @Description 方法反射调用同一入口
 */
public class MethodInvoke {

    private Map<String, Object> handlerMap = new HashMap<>();

    public MethodInvoke(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public Object invoke(RpcRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //1. 查询服务service是否存在
        String className = request.getClassName();
        String version = request.getVersion();
        if(!StringUtils.isEmpty(version)) {
            className += "-" + version ;
        }

        Object service = handlerMap.get(className);
        if(null == service) {
            throw new RuntimeException("No this service = " + className) ;
        }

        //2. 处理参数类型, 用于确定方法
        Method method ;
        Object[] args = request.getArgs();
        Class<?> clazz = Class.forName(request.getClassName());
        if(null != args) { //2.1 存在参数
            Class[] argTypes = new Class[args.length] ;
            for (int i = 0 , len = args.length ; i < len ; i++) {
                argTypes[i] = args[i].getClass() ;
            }

            method = clazz.getMethod(request.getMethodName(), argTypes);
        } else { // 不存在参数
            method = clazz.getMethod(request.getMethodName()) ;
        }

        //3. 反射调用,并返回结果
        return method.invoke(service, args) ;
    }
}
