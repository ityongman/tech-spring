package com.ityongman.delegate.simple;

public class Boss {
    public void doWork(Leader leader, String cmd){
        leader.doWork(cmd);
    }
}
