package com.ityongman.delegate.simple;

public class DelegateWorkerTest {
    public static void main(String[] args) {
        Boss boss = new Boss();
        Leader leader = new Leader();

        boss.doWork(leader, "java");
    }
}
