package com.ityongman.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @Author shedunze
 * @Date 2020-03-10 15:50
 * @Description
 */
public class RandomLoadBalance implements ILoadBalance {
    @Override
    public String selectServer(List<String> serviceRepos) {
        int repoSize = serviceRepos.size();

        Random r = new Random();
        return serviceRepos.get(r.nextInt(repoSize));
    }
}
