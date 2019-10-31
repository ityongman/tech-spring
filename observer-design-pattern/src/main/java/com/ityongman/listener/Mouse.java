package com.ityongman.listener;

import com.ityongman.constant.EventType;

public class Mouse extends EventListener{
    public void click() {
        System.out.println("Mouse click ...");
        trigger(EventType.ON_CLICK);
    }

    public void doubleClick() {
        System.out.println("Mouse doubleClick ...");
        trigger(EventType.ON_DOUBLE_CLICK);
    }

    public void up() {
        System.out.println("Mouse up ...");
        trigger(EventType.ON_UP);
    }
}
