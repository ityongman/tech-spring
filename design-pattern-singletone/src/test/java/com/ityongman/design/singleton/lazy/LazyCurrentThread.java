package com.ityongman.design.singleton.lazy;

public class LazyCurrentThread implements Runnable{
    public void run() {
        LazySimpleSingleton singleton = LazySimpleSingleton.getSingleton();
        System.out.println(Thread.currentThread().getName() + ":" + singleton);
    }
}
