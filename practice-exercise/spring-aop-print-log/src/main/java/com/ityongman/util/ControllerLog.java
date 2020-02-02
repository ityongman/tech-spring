package com.ityongman.util;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Order(1)
@Component
@Aspect
public class ControllerLog {
    private Logger log = LoggerFactory.getLogger(ControllerLog.class) ;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController(){
    }

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() {
    }

    @Around("restController() || controller()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURI();
        // 应该只打印header中的有用信息，当以后启用header传输参数时，再确定打印哪些
        StringBuilder logStr = new StringBuilder("request:");
        //1. 输出request Get请求参数 request.getParameterMap()
        logStr.append(url).append(";get args:").append(JSON.toJSONString(request.getParameterMap()));
        log.info("Request get params : {}" , logStr.toString());

        //2. POST请求参数
        Object[] objParams = joinPoint.getArgs() ;
        if(null != objParams) {
            log.info("Request post params : {} ", JSON.toJSONString(objParams[0])); // 打印第一个参数, JsonBody只要求有一个参数
        }

        // 执行业务逻辑
        Object result = joinPoint.proceed();
        //响应结果
        log.info("response {}：{}, cost {}", url, JSON.toJSONString(result), (System.currentTimeMillis() - startTime));

        return result;
    }
}
