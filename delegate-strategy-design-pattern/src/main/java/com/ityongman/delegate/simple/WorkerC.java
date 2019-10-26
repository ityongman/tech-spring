package com.ityongman.delegate.simple;

public class WorkerC implements IWorker {
    @Override
    public void doWork(String cmd) {
        System.out.println("WorkerA do work " + cmd);
    }
}
