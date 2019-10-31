package com.ityongman;

import com.google.common.eventbus.EventBus;
import com.ityongman.guava.GuavaEvent;

public class GuavaEventTest {
    public static void main(String[] args) {
        EventBus bus = new EventBus(); // 类似EventListener作用

        GuavaEvent event = new GuavaEvent();
        bus.register(event); // 添加信息到Multimap中, 类似EventListener中的HashMap
        bus.post("Tom and Jerry"); // 事件触发, 类似EventListener中的trigger
    }
}
