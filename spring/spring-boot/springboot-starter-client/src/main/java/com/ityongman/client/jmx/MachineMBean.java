package com.ityongman.client.jmx;

/**
 * @Author shedunze
 * @Date 2020-03-10 09:29
 * @Description 保存系统参数相关实体类
 */
public interface MachineMBean {
    /**
     * 获取机器核数
     */
    int getCpuCore();
    /**
     * 获取当前操作线程
     */
    String getCurrThread();
    /**
     * 远程停止服务
     */
    void shutDown();
}
