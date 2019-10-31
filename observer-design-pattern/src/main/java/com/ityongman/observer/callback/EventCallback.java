package com.ityongman.observer.callback;

import com.ityongman.observer.event.Event;

public class EventCallback implements IEventCallback{

    @Override
    public void click(Event e) {
        System.out.println("click event + " + e.toString());
    }

    @Override
    public void doubleClick(Event e) {
        System.out.println("doubleClick event + " + e.toString());
    }

    @Override
    public void up(Event e) {
        System.out.println("up event + " + e.toString());
    }
}
