package com.ityongman;

import com.ityongman.observer.callback.EventCallback;
import com.ityongman.observer.constant.EventType;
import com.ityongman.observer.listener.Mouse;

public class EventTest {
    public static void main(String[] args) {
        Mouse mouse = new Mouse(); // 事件源 source
        EventCallback callback = new EventCallback(); // 通知的目标对象 target

        mouse.addListener(EventType.ON_CLICK, callback); // 将监听对象target = callback存入listener map中

        mouse.click(); // 触发事件源, listener通知观察者 EventCallback, 执行对应逻辑
    }
}
