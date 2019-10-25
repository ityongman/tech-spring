package com.ityongman.forkjoin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class CountTask extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 10 ; // 1w
    private static final int SPLITTASK = 10 ; // 需要拆分的任务数
    private int startNum ;
    private int endNum ;

    public CountTask(int startNum, int endNum) {
        this.startNum = startNum ;
        this.endNum = endNum ;
    }

    protected Integer compute() {
        int totalNum = 0 ;

        if ((endNum - startNum) <= THRESHOLD) { // 计算区间是否超过阈值
            totalNum = countNum(startNum, endNum);
        } else { // 超过临界值, 数据拆分
            totalNum = numSplit(startNum, endNum);
        }

        return totalNum;
    }

    //进行服务拆分
    private int numSplit(int startNum, int endNum) {
        int total = 0 ;

        //1. 计算步长
        int step = (startNum + endNum) / SPLITTASK ;
        List<CountTask> countTasks = new ArrayList<>();

        int newStart = startNum ;
        int newEnd = newStart + step ;
        //服务拆分
        for(int i = 0 ; i < SPLITTASK ;i++) {
            if(newEnd > endNum) { // 数据不能超过最大数据
                newEnd = endNum ;
            }
            CountTask subCountTask = new CountTask(newStart, newEnd);
            newStart = newEnd + 1 ; // 更新数据起始位置
            newEnd += step ; // 更新数据截至位置
            countTasks.add(subCountTask);

            subCountTask.fork() ;
        }

        //结果合并
        for(CountTask countTask : countTasks) {
            total += countTask.join();
        }
        return total;
    }

    // 计算数值大小
    private int countNum(int startNum, int endNum) {
        int total = 0 ;
        for (int i = startNum ; i <= endNum ; i++) {
            total += i ;
        }

        return total ;
    }
}
