package com.ityongman.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @Author shedunze
 * @Date 2020-02-02 13:32
 * @Description 异步处理业务逻辑的线程
 */
public class ProcessorHandler implements Runnable {
    private Socket socket ;
    private Object service ;

    public ProcessorHandler(Object service, Socket socket) {
        this.socket = socket;
        this.service = service ;
    }

    /**
     * 服务器处理需要响应的数据
     */
    @Override
    public void run() {
        ObjectInputStream input = null ;
        ObjectOutputStream output = null ;
        try {
            //1. 获取输入流,从输入流中提取需要的信息
            input = new ObjectInputStream(socket.getInputStream());
            RpcRequest rpcRequest = (RpcRequest) input.readObject();
            //2. 反射调用方法
            Object result = invokeMethod(rpcRequest);
            //3. 获取输出流, 将响应数据返回
            output = new ObjectOutputStream(socket.getOutputStream());
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
        }finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object invokeMethod(RpcRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //1. 提取参数
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Object[] args = request.getArgs();
        Class[] argTypes = new Class[args.length] ;
        for (int i = 0 , len = args.length ; i < len ; i++) {
            argTypes[i] = args[i].getClass() ;
        }
        //2. 反射调用
        //2.1 获取类对象 HelloServiceImpl
        Class<?> clazz = Class.forName(className);
        //2.2 获取需要调用的方法 sayHello
        Method method = clazz.getMethod(methodName, argTypes);
        //2.3 反射调用方法
        Object result = method.invoke(service/**clazz.newInstance()*/, args);

        return result ;
    }
}
