package com.ityongman.exam01;

import com.ityongman.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author shedunze
 * @Date 2020-01-30 11:22
 * @Description
 */
public class ServerSocketProvider {
    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(8080)/**服务端 创建服务对象*/) {
            Socket socket = serverSocket.accept(); // 阻塞监听
            //创建输入流 读取网络流对象
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            //流转为对象
            User user = (User) objectInputStream.readObject();
            System.out.println(user);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
