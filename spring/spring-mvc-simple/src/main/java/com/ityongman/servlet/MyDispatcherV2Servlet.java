package com.ityongman.servlet;

import com.ityongman.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDispatcherV2Servlet extends HttpServlet {
    // 存储配置文件信息
    private Properties configFile = new Properties();

    // 保存所有后期可能需要实例化的类路径信息
    private List<String> clazzNames = new ArrayList<>();

    //
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    //url <-> method
    private List<HandlerMapping> handlerMapping = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /**
         * 通过委派模式, 进行业务分发
         */
        try {
            this.doDispatcher(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(" 500 Server error , stack trace --> " + Arrays.toString(e.getStackTrace()));
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        /**
         * 通过模版方式, 进行资源初始化
         */
        /**
         * 1. 加载配置文件, 原生配置文件是xml格式,
         *  这里为了处理方便, 使用properties文件
         */
        initConfigFile(config.getInitParameter("contextConfigLocation"));

        /**
         * 2. 根据配置文件配置的扫描路径, 进行文件扫描, 保存哪些可能需要进行实例化
         */
        initFileScanInfo(configFile.getProperty("scanPackage"));

        /**
         * 3. 初始化所有扫描类的实例
         */
        initInstance();
        /**
         * 4. 完成类实例相关属性的依赖注入
         */
        initInstanceAttr();
        
        /**
         * 5. 初始化 RequestMapping相对应的HandlerMapping
         */
        initHandlerMapping();
        
        System.out.println("configuration init end ...");
    }

    //从servlet container 配置的contextConfigLocation路径信息, 加载配置文件
    private void initConfigFile(String contextConfigLocation) {
        try(InputStream fis = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation)){
            configFile.load(fis);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据配置文件配置的扫描路径, 进行文件扫描
    private void initFileScanInfo(String scanPackage) {
        //1. 将路径信息中 "." 修改为 "/"
        StringBuilder sb = new StringBuilder();
        sb.append("/")
            .append(scanPackage.replaceAll("\\.", "/"));
        String clazzPath = sb.toString();
        //2. 文件信息加载
        URL url = this.getClass().getClassLoader().getResource(clazzPath);
        //3. 创建文件
        File directory = new File(url.getFile());
        //4. 遍历文件信息
        for(File file : directory.listFiles()) {
            StringBuilder innerSb = new StringBuilder();
            innerSb.append(scanPackage).append(".").append(file.getName());
            if(file.isDirectory()){
                initFileScanInfo(innerSb.toString());
            } else {
                if(!file.getName().endsWith(".class")) {
                    continue; // 非class文件不处理
                }
                String clazzName = innerSb.toString().replaceAll(".class", "");
                clazzNames.add(clazzName);
            }
        }
    }

    //初始化所有扫描类的实例
    private void initInstance() {
        // 只对添加了注解的类进行实例化 @MyController @MyService
        if (clazzNames.isEmpty()) {return;}

        try {
            for (String clazzName : clazzNames){
                // 根据类名字路径信息, 获得类信息
                Class<?> clazz = Class.forName(clazzName);
                //解析 @MyController 注解
                if(clazz.isAnnotationPresent(MyController.class)) {
                    Object instance = clazz.newInstance();
                    addBeanWithSimpleName(clazz, instance);
                }
                if (clazz.isAnnotationPresent(MyService.class)){
                    Object instance = clazz.newInstance();
                    // 解析simpleName
                    addBeanWithSimpleName(clazz, instance);
                    // 注解 value 属性添加bean信息
                    addBeanWithAnnotationValue(clazz, instance);
                    // 根据类型注入实现类
                    for (Class<?> anInterface : clazz.getInterfaces()) {
                        if(beanMap.containsKey(anInterface.getName())) {
                            throw new IllegalStateException("The beanName is exists!!");
                        }
                        beanMap.put(anInterface.getName(), instance);
                    }

                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void addBeanWithAnnotationValue(Class<?> clazz, Object instance) throws InstantiationException, IllegalAccessException {
        MyService myService = clazz.getAnnotation(MyService.class);
        String serviceValueName = myService.value();
        if (serviceValueName != null && !serviceValueName.isEmpty()) {
            beanMap.put(serviceValueName, instance);
        }
    }

    private void addBeanWithSimpleName(Class<?> clazz, Object instance) throws InstantiationException, IllegalAccessException {
        String beanName = toFirstChar2Lower(clazz.getSimpleName());
        beanMap.put(beanName, instance);
    }

    //完成类实例相关属性的依赖注入
    private void initInstanceAttr() {
        if (beanMap.isEmpty()){return ;}

        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields){
                if(!field.isAnnotationPresent(MyAutowired.class)) {return ;}

                MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                String beanName = myAutowired.value();
                // 未添加value属性值
                if("".equals(beanName)){
                    beanName = field.getType().getName();
                }

                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    //初始化 RequestMapping相对应的HandlerMapping
    private void initHandlerMapping() {
        if (beanMap.isEmpty()){return ;}

        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(MyController.class)) { return ;}

            StringBuilder sbUrl = new StringBuilder("/") ;
            if(clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                sbUrl.append(requestMapping.value()) ;
            }

            StringBuilder innerSb ;
            Method[] methods = clazz.getMethods();
            for(Method method : methods){
                if(!method.isAnnotationPresent(MyRequestMapping.class)) {return ;}

                innerSb = new StringBuilder(sbUrl);
                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                innerSb.append(requestMapping.value());
                String url = innerSb.toString().replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(url);
                handlerMapping.add(new HandlerMapping(entry.getValue(), method, pattern));
            }
        }

    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1. 从HandlerMapping 中查询有无 handler 处理器
        HandlerMapping handler = getHandler(req, resp);
        if (handler == null) {
            resp.getWriter().write("404 Not Found !!!");
            return ;
        }
        //2. 获取匹配方法的参数列表
        Class<?>[] parameterTypes = handler.method.getParameterTypes();
        Object[] parameterValues = new Object[parameterTypes.length];
        //3. 获取请求的参数列表
        Map<String/*paramName*/, String[]/*paramValue*/> reqParams = req.getParameterMap();
        //4. 解析请求参数
        //TODO 这里有一个问题, 请求参数 >= 方法参数, 并且包含所有方法参数也是可以调用成功的, 不是绝对匹配
        for (Map.Entry<String, String[]> entry : reqParams.entrySet()) {
            // 参数值
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", ",");
            //
            if(!handler.paramsMappingIndex.containsKey(entry.getKey())) {continue;}
            Integer index = handler.paramsMappingIndex.get(entry.getKey());
            parameterValues[index] = convert(parameterTypes[index], value);
        }
        //5. 处理HttpServletRequest 、 HttpServletResponse
        Integer reqIndex = handler.paramsMappingIndex.get(HttpServletRequest.class.getName());
        parameterValues[reqIndex] = req ;
        Integer respIndex = handler.paramsMappingIndex.get(HttpServletResponse.class.getName());
        parameterValues[respIndex] = resp ;

        handler.method.invoke(handler.ctrInstance, parameterValues);
    }

    private Object convert(Class<?> parameterType, String value) {
        if (Integer.class == parameterType) {
            return Integer.valueOf(value);
        }
        if (Double.class == parameterType) {
            return Double.valueOf(value);
        }
        return value ;
    }

    private HandlerMapping getHandler(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (handlerMapping.isEmpty()){
            return null ;
        }
        //1. 返回站点根目录
        String contextPath = req.getContextPath();
        //2. 返回路径信息
        String uri = req.getRequestURI();
        //3. 处理掉根目录信息 和  多"/"信息
        uri = uri.replaceAll(contextPath, "").replaceAll("/+", "/");
        //4.1 如果映射关系中没有url, 返回
        for(HandlerMapping hm : handlerMapping) {
            Matcher matcher = hm.pattern.matcher(uri);
            if (!matcher.matches()) {continue;}

            return hm ;
        }

        return null;
    }

    private String toFirstChar2Lower(String beanName) {
        char[] chars = beanName.toCharArray();
        chars[0] += 32 ;

        return String.valueOf(chars);
    }


    private class HandlerMapping {
        // ctl 对应的实例对象
        protected  Object ctrInstance ;
        // method 方法
        protected Method method ;
        //解析pattern, uri 映射关系, 请求的时候通过这个pattern进行匹配
        protected Pattern pattern;
        //参数顺序
        protected Map<String, Integer> paramsMappingIndex ;

        public HandlerMapping(Object ctrInstance, Method method, Pattern pattern) {
            this.ctrInstance = ctrInstance ;
            this.method = method ;
            this.pattern = pattern ;

            paramsMappingIndex = new ConcurrentHashMap<>();
            parseParamsMappingIndex(method);
        }

        private void parseParamsMappingIndex(Method method) {
            //1. 处理HttpServletRequest、HttpServletResponse
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0 , len = parameterTypes.length ; i < len ; i++) {
                Class<?> parameterType = parameterTypes[i];
                if(parameterType == HttpServletRequest.class
                        || parameterType == HttpServletResponse.class) {
                    paramsMappingIndex.put(parameterType.getName(), i);
                }
            }

            //2. 处理带有注解 @MyRequestParam 的参数
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for(int i = 0 , len = parameterAnnotations.length ; i < len ; i++) {
                for(Annotation annotation : parameterAnnotations[i]) {
                    if(annotation instanceof MyRequestParam) {
                        String paramName = ((MyRequestParam) annotation).value();
                        if (!"".equals(paramName.trim())) {
                            paramsMappingIndex.put(paramName, i);
                        }
                    }
                }
            }
        }
    }
}
