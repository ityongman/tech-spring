package com.ityongman.loadbalance;

import java.util.List;

/**
 * @Author shedunze
 * @Date 2020-03-10 15:50
 * @Description
 */
public interface ILoadBalance {
    /**
     * 负载均衡获取 服务器 方法
     * @param serviceRepos 服务器信息
     * @return
     */
    String selectServer(List<String> serviceRepos);
}
