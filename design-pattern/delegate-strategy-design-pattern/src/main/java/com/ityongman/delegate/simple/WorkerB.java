package com.ityongman.delegate.simple;

public class WorkerB implements IWorker {
    @Override
    public void doWork(String cmd) {
        System.out.println("WorkerA do work " + cmd);
    }
}
