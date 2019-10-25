package com.ityongman.design.singleton.lazy;

import com.ityongman.design.singleton.seriable.SeriableSingleton;

import java.io.*;

/**
 * 对饿汉式,进行序列化破解
 */
public class SeriableSingletonTest {
    public static void main(String[] args) {
        SeriableSingleton ss1 = SeriableSingleton.getSingleton();
        SeriableSingleton ss2 = null ;

        try {
            //1. 将单例文件输出到磁盘
            FileOutputStream fopt = new FileOutputStream("SeriableSingleton.serial");
            ObjectOutputStream oos = new ObjectOutputStream(fopt);

            oos.writeObject(ss1);
            fopt.close();
            oos.close();

            //2. 从磁盘读取前面输出的文件
            FileInputStream fis = new FileInputStream("SeriableSingleton.serial");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ss2 = (SeriableSingleton) ois.readObject();

            fis.close();
            ois.close();

            System.out.println(ss1);
            System.out.println(ss2);
            System.out.println(ss1 == ss2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
