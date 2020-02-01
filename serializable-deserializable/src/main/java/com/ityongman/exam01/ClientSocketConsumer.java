package com.ityongman.exam01;

import com.ityongman.model.User;

import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Author shedunze
 * @Date 2020-01-30 11:24
 * @Description
 */
public class ClientSocketConsumer {
    public static void main(String[] args) {

        try (Socket socket = new Socket("127.0.0.1", 8080);
             ObjectOutputStream out =new ObjectOutputStream(socket.getOutputStream()) ) {
            User user = new User();
            user.setUserName("Johy");
            user.setAge(15);

            out.writeObject(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
