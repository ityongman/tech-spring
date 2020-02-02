package com.ityongman.observer.callback;

import com.ityongman.observer.event.Event;

public interface IEventCallback {
    /**
     * 单击
     */
    void click(Event e);

    /**
     * 双击
     */
    void doubleClick(Event e);

    /**
     * 上移
     */
    void up(Event e);
}
