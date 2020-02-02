package com.ityongman.current;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ConcurrentExecutor {

    /**
     *
     * @param handle 逻辑处理接口
     * @param executeCount 最大可处理请求数
     * @param currCount 最大可并发请求数
     */
    public static void execute(RunHandler handle, int executeCount, int currCount) throws InterruptedException {
        // 信号量, 用于控制最大并发数
        final Semaphore sp = new Semaphore(currCount) ;

        // CountDownLatch处理最大请求数
        CountDownLatch cd = new CountDownLatch(executeCount);

        //缓存线程池
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0 ; i < executeCount ; i++){
            es.execute(() -> {
                try {
                    sp.acquire(); // 获取信号量

                    handle.handler(); // 处理逻辑

                    sp.release(); // 释放信号量
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                cd.countDown(); // 计数减一
            });
        }
        cd.await();// 请求超过最大限制, 等待

        es.shutdown();
    }


    public interface RunHandler{
        void handler();
    }
}
