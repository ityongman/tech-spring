package com.ityongman.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @Author shedunze
 * @Date 2020-03-10 14:34
 * @Description 通过 curator 操作 zookeeper 服务器
 */
public class ZkRegistryCurator implements IZkRegistry {
    CuratorFramework curatorFramework = null ;

    {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZkConfig.ZK_SERVER_ADDR)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("registry").build();

        curatorFramework.start();
    }

    /**
     * 服务器启动后 向 zookeeper 注册服务实现方法
     * @param serviceName com.ityongman.service.IHelloService-V1.0
     * @param serviceAddr 127.0.0.1:2181
     */
    @Override
    public void registry(String serviceName, String serviceAddr) {
        //1. 组建 zookeeper 可以识别的服务路径信息
        String servicePath = "/" + serviceName ;
        try {
            //2. 判断服务节点是否存在, 如果不存在则创建
            if(null == curatorFramework.checkExists().forPath(servicePath)) {
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }

            //3. 创建叶子节点
            String addressPath = servicePath + "/" + serviceAddr ;
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath);

            System.out.println("服务地址: " + addressPath + " 注册成功!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
