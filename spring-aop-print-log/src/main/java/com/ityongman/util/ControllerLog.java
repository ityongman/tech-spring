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
import java.util.Map;

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
        long t = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURI();
        // 应该只打印header中的有用信息，当以后启用header传输参数时，再确定打印哪些
        StringBuilder logStr = new StringBuilder("request:");
        //1. 输出request Get请求参数 request.getParameterMap()
        logStr.append(url).append(";args:").append(JSON.toJSONString(request.getParameterMap()));
        log.info("Get request params : {}" , logStr.toString());

        //2. POST请求参数
        Object[] objParams = joinPoint.getArgs() ;
        if(null != objParams) {
            Map<String, String> postBodyParams = resolvePostBodyParam(objParams[0]);
        }
        log.info("args --> ", objParams.toString());
        // 执行业务逻辑
        Object result = joinPoint.proceed();
        //响应结果
        long endTime = System.currentTimeMillis();
        long duration = (endTime - t);
        log.info("response {}：{}, cost {}", url, JSON.toJSONString(result), duration);

        return result;
    }


    public Map<String, String> resolvePostBodyParam(Object obj) {
        return  null ;
    }
}
