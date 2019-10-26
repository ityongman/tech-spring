package com.ityongman.design.singleton.lazy;

import com.ityongman.threadlocal.ThreadLocalSingleton;

public class ThreadLocalSingletonTest {
    public static void main(String[] args) {
        //1.
//        for (int i = 0 ; i < 10 ; i++) {
//            ThreadLocalSingleton instance = ThreadLocalSingleton.getInstance();
//            System.out.println(instance);
//        }

        //2.
        Thread t1 = new Thread(() -> {
            for (int i = 0 ; i < 5 ; i++) {
                ThreadLocalSingleton instance = ThreadLocalSingleton.getInstance();
                System.out.println(Thread.currentThread() + "" +instance);
            }
        });

        //3.
        Thread t2 = new Thread(() -> {
            for (int i = 0 ; i < 5 ; i++) {
                ThreadLocalSingleton instance = ThreadLocalSingleton.getInstance();
                System.out.println(Thread.currentThread() + "" +instance);
            }
        });

        t1.start();
        t2.start();

        System.out.println("End...");
    }
}
