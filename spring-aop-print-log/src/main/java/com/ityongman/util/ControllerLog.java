package com.ityongman.util;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Order(1)
@Component
@Aspect
public class ControllerLog {
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController(){
    }

    @Pointcut("within(@org.springframework.stereotype.Controller)")
    public void controller() {
    }

    @Around("restController() || controller()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable{
        long t = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURI();
        StringBuilder logStr = new StringBuilder("request:");
        logStr.append(url).append(";args:").append(JSON.toJSONString(request.getParameterMap()));
        // 应该只打印header中的有用信息，当以后启用header传输参数时，再确定打印哪些
        System.out.println(logStr.toString());
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long duration = (endTime - t);
        System.out.println(String.format("response %s：%s, cost %s", url, JSON.toJSONString(result), duration));
        return result;
    }
}
