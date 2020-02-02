package com.ityongman.config;

import com.alibaba.fastjson.JSON;
import com.ityongman.constants.Constants;
import com.ityongman.util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RedisHyperLogLogAspect {
    @Autowired
    private RedisTemplate redisTemplate ;

    @Pointcut("@annotation(com.ityongman.config.RedisHyperLogLog)")
    public void redisHyperLog(){}

    /**
     * 以articleId + IP为基准处理
     * 也可以通过其它的方式, 比如: userId + articleId
     * 具体采取的策略可以根据业务来决定
     */
    @Around("redisHyperLog()")
    public Object hyperAround(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs(); // NOTE: 这里以统计微博阅读量为例说明
        log.info("request args = ", JSON.toJSONString(args));

        Object obj = null;
        try{
            if(null != args) {
                Object articleId = args[0];
                String ipAddr = IPUtil.getIPAddr();
                String redisKey = Constants.REDIS_HYPER_LOG_ARTICLE_PREFIX + articleId ;

                Long addRet = redisTemplate.opsForHyperLogLog().add(redisKey, ipAddr);
                if(0 == addRet) {
                    log.info("current ip = {} had add before", ipAddr);
                }
                obj = joinPoint.proceed() ;
            }
        }catch (Throwable e) {
            log.error("processor error , e = {}", e);
        }
        return obj ;
    }
}
