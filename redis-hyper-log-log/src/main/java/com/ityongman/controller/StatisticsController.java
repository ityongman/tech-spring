package com.ityongman.controller;

import com.ityongman.config.RedisHyperLogLog;
import com.ityongman.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class StatisticsController {
    @Autowired
    private RedisTemplate redisTemplate ;

    @RedisHyperLogLog
    @RequestMapping("query/{articleId}")
    public String queryArticleById(@PathVariable("articleId") Long articleId) {
        Long querySize = redisTemplate.opsForHyperLogLog().size(Constants.REDIS_HYPER_LOG_ARTICLE_PREFIX + articleId);
        return "article total query count = " + querySize ;
    }
}
