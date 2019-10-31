package com.ityongman.listener;

import com.ityongman.event.Event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EventListener {
    /**
     * 容器: 用来存放事件
     * 一般情况都是通过容器来处理, Map、List之类
     */
    private Map<String, Event> events = new HashMap<>();

    public void addListener(String eventType, Object target) {
        try {
            addListener(
                    eventType,
                    target,
                    target.getClass().getMethod(eventType, Event.class)
            );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void addListener(String eventType, Object target, Method callback) {
        events.put(eventType, new Event(target, callback));
    }

    public void trigger(String eventType) {
        if (events.containsKey(eventType)) {
            trigger(events.get(eventType).setTrigger(eventType));
        }
    }

    private void trigger(Event event) {
        event.setSource(this);
        event.setTime(System.currentTimeMillis());

        try{
            if(event.getCallback() != null) {
                event.getCallback().invoke(event.getTarget(), event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
