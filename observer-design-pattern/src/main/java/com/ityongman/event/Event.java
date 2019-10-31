package com.ityongman.event;

import java.lang.reflect.Method;

/**
 * 事件格式定义
 */
public class Event {
    /**
     * 事件源
     */
    private Object source;
    /**
     * 事件通知对象, 事件触发的时候需要通知谁
     */
    private Object target ;
    /**
     * 事件触发的时候, 需要做的回调操作
     */
    private Method callback ;
    /**
     * 触发的是什么事件
     */
    private String trigger ;
    /**
     * 触发的时间 timestamp
     */
    private long time ;

    /**
     * @param target
     * @param callback
     */
    public Event(Object target, Method callback) {
        this.target = target;
        this.callback = callback;
    }

    public Object getSource() {
        return source;
    }

    public Event setSource(Object source) {
        this.source = source;
        return this ;
    }

    public Object getTarget() {
        return target;
    }

    public Event setTarget(Object target) {
        this.target = target;
        return this;
    }

    public Method getCallback() {
        return callback;
    }

    public Event setCallback(Method callback) {
        this.callback = callback;
        return this ;
    }

    public String getTrigger() {
        return trigger;
    }

    public Event setTrigger(String trigger) {
        this.trigger = trigger;
        return this ;
    }

    public long getTime() {
        return time;
    }

    public Event setTime(long time) {
        this.time = time;
        return this ;
    }

    @Override
    public String toString() {
        return "Event{" +
                "source=" + source +
                ", target=" + target +
                ", callback=" + callback +
                ", trigger='" + trigger + '\'' +
                ", time=" + time +
                '}';
    }
}
