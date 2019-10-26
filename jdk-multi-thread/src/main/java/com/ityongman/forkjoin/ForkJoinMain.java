package com.ityongman.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * 掌握对ForkJoin的使用方式
 *
 * 思想: 将一个大的任务拆分成若干个子任务分别执行, 最后统计所有子任务的结果
 * 例: 向数据库中 insert 100w条数据 --> [1-300000], [300001-600000], [600001-900000], [900001-1000000]
 *      拆分成若干子任务进行数据插入, 最后统计插入结果
 *      总结: 任务拆分、结果合并
 *  NOTE: 1. 注意和Batch插入的区别
 */
public class ForkJoinMain {
    /**
     * 这里以计算1-200000 之间数据之和进行举例
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask task = new CountTask(0, 100);
        ForkJoinTask<Integer> forkResult = forkJoinPool.submit(task);

        Integer total = forkResult.get();
        System.out.println("result --> " + total);
    }
}
