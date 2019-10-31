package com.ityongman.guava;

import com.google.common.eventbus.Subscribe;

public class GuavaEvent {
    @Subscribe
    public void subscribe(String msg) { // 1. 接收的是一个Object 2. 处理逻辑和方法名没有关系, 需要加注解@Subscribe
        System.out.println("process guava subscribe loslogic , param = " + msg);
    }
}
