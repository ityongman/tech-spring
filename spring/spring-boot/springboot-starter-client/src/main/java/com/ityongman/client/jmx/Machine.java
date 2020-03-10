package com.ityongman.client.jmx;

/**
 * @Author shedunze
 * @Date 2020-03-10 09:33
 * @Description 机器操作
 */
public class Machine implements MachineMBean {
    @Override
    public int getCpuCore() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Override
    public String getCurrThread() {
        return Thread.currentThread().getName();
    }

    @Override
    public void shutDown() {
        System.exit(0);
    }
}
