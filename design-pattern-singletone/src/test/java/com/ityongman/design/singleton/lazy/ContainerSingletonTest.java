package com.ityongman.design.singleton.lazy;

import com.ityongman.current.ConcurrentExecutor;
import com.ityongman.design.singleton.register.ContainerSingleton;

public class ContainerSingletonTest {

    public static void main(String[] args) throws InterruptedException {
        ConcurrentExecutor.execute(() -> {
            Object obj = ContainerSingleton.getBean("com.ityongman.entity.User");
            System.out.println(obj);
        }, 10, 5);
    }
}
