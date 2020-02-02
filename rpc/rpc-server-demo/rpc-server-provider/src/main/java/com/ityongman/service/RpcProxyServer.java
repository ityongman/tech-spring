package com.ityongman.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author shedunze
 * @Date 2020-02-02 13:23
 * @Description 定义线程池, 将监听和处理都是阻塞的方式改造为 ,
 * 监听阻塞accept(), 处理异步的方式 ExecutorServices
 */
public class RpcProxyServer {
    // 异步处理任务的线程池
    ExecutorService pool = Executors.newCachedThreadPool();

    public void publish(Object service, int port) {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();

                pool.execute(new ProcessorHandler(service,socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
