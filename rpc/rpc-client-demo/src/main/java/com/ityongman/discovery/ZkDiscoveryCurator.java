package com.ityongman.discovery;

import com.ityongman.loadbalance.ILoadBalance;
import com.ityongman.loadbalance.RandomLoadBalance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author shedunze
 * @Date 2020-03-10 14:34
 * @Description 通过 curator 操作 zookeeper 服务器
 */
public class ZkDiscoveryCurator implements IZkDiscovery {
    private List<String> serviceRepos = new ArrayList<>() ;

    private CuratorFramework curatorFramework = null ;

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
     */
    @Override
    public String discovery(String serviceName) {
        /**
         * 1. 组装服务地址
         * /registry/com.ityongman.service.IHelloService
         */
        String servicePath = "/" + serviceName ;

        //2. 查询服务是否存在
        if(serviceRepos.isEmpty()) {
            try {
                serviceRepos = curatorFramework.getChildren().forPath(servicePath) ;
                //3. 注册节点变更监听
                registryWatch(servicePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //4. 针对已有的节点做负载均衡 用于测试负载均衡
        ILoadBalance random = new RandomLoadBalance();
        return random.selectServer(serviceRepos);
    }

    private void registryWatch(String servicePath) throws Exception {
        //1. 添加监听
        PathChildrenCacheListener cacheListener = (client, event) -> {
            System.out.println("客户端接收到节点变更通知");
            serviceRepos = client.getChildren().forPath(servicePath) ;
        };
        //2. 创建 PathChildrenCache 对象
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, servicePath, true);
        childrenCache.getListenable().addListener(cacheListener);
        childrenCache.start();
    }
}
