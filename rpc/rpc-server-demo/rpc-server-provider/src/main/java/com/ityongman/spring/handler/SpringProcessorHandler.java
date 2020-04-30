package com.ityongman.spring.handler;

import com.ityongman.service.RpcRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

/**
 * @Author shedunze
 * @Date 2020-02-02 16:21
 * @Description 方法1, 配合阻塞式 服务处理的handler
 */
public class SpringProcessorHandler implements Runnable {
    private Socket socket ;

    private Map<String, Object> handlerMap ;

    public SpringProcessorHandler(Socket socket, Map<String, Object> handlerMap) {
        this.socket = socket ;
        this.handlerMap = handlerMap ;
    }

    @Override
    public void run() {
        try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
            //1. 处理请求结果
            RpcRequest request = (RpcRequest) input.readObject();
            Object result = invoke(request) ;

            //2. 处理响应
            output.writeObject(result);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Object invoke(RpcRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
