package com.ityongman.registry;

/**
 * @Author shedunze
 * @Date 2020-03-10 14:30
 * @Description 操作 zookeeper 接口类
 */
public interface IZkRegistry {
    void registry(String serviceName, String serviceAddr) ;
}
