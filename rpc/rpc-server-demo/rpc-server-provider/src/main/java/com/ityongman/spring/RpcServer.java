package com.ityongman.spring;

import com.ityongman.spring.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author shedunze
 * @Date 2020-02-02 16:17
 * @Description
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {
    private int port ;

    private Map<String, Object> handlerMap = new HashMap<>() ;

    ExecutorService pool = Executors.newCachedThreadPool();

    public RpcServer(int port) {
        this.port = port;
    }

    /**
     * 属性处理完之后, 启动服务
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try(ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(new SpringProcessorHandler(socket,handlerMap));
            }
        }
    }

    /**
     * bean加载完之后, 加载所有的bean信息到map中保存
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);

                //1. 接口类定义
                String serviceName = rpcService.value().getName();
                //2。版本定义
                String version = rpcService.version();
                if(!StringUtils.isEmpty(version)) {
                    serviceName = serviceName + "-" + version ;
                }

                handlerMap.put(serviceName, serviceBean) ;
            }
        }
    }
}
