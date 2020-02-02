package com.ityongman.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Author shedunze
 * @Date 2020-02-02 14:24
 * @Description
 */
public class RpcTransport {
    private String host ;
    private int port ;

    public RpcTransport(String host, int port) {
        this.host = host ;
        this.port = port ;
    }


    public Object send(RpcRequest request) {
        Object result = null ;
        ObjectOutputStream outPut = null ;
        ObjectInputStream input = null ;

        try (Socket socket = new Socket(host, port)) {
            //1. 发送数据
            outPut = new ObjectOutputStream(socket.getOutputStream()) ;
            outPut.writeObject(request);
            outPut.flush();
            //2. 接收数据
            input = new ObjectInputStream(socket.getInputStream()) ;
            result = input.readObject();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result ;
    }
}
