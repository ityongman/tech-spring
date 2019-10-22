package com.ityongman.design.singleton.lazy;

public class LazySimpleSingletoneTest {
    public static void main(String[] args) {
        Thread t1 = new Thread(new LazyCurrentThread());
        Thread t2 = new Thread(new LazyCurrentThread());

        t1.start();
        t2.start();

        System.out.println("End....");
    }
}
