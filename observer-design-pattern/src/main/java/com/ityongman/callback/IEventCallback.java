package com.ityongman.callback;

import com.ityongman.event.Event;

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
