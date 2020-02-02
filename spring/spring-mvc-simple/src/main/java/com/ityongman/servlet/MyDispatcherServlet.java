package com.ityongman.servlet;

import com.ityongman.annotation.MyAutowired;
import com.ityongman.annotation.MyController;
import com.ityongman.annotation.MyRequestMapping;
import com.ityongman.annotation.MyService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyDispatcherServlet extends HttpServlet {
    // 存储配置文件信息
    private Properties configFile = new Properties();

    // 保存所有后期可能需要实例化的类路径信息
    private List<String> clazzNames = new ArrayList<>();

    //
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    //url <-> method
    private Map<String, Method> handlerMapping = new HashMap<>();

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
                if("".equals(beanName)){ // 未添加value属性值
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

            Method[] methods = clazz.getMethods();
            for(Method method : methods){
                if(!method.isAnnotationPresent(MyRequestMapping.class)) {return ;}

                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                sbUrl.append(requestMapping.value());
                String url = sbUrl.toString().replaceAll("/+", "/");
                handlerMapping.put(url, method);
            }
        }

    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1. 返回站点根目录
        String contextPath = req.getContextPath();
        //2. 返回路径信息
        String uri = req.getRequestURI();
        //3. 处理掉根目录信息 和  多"/"信息
        uri = uri.replaceAll(contextPath, "").replaceAll("/+", "/");
        //4.1 如果映射关系中没有url, 返回
        if (!handlerMapping.containsKey(uri)) {
            resp.getWriter().write("404 Not Found !!!");
            return ;
        }
        //4.2 如果有映射关系进行匹配处理
        Method method = this.handlerMapping.get(uri); // 找到 uri 对应的 method方法
        //5. 获取形参的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        //6. 请求传入的参数列表
        Map<String, String[]> parameterMap = req.getParameterMap();
        //7. 简化处理, 获取类instance信息
        String beanName = toFirstChar2Lower(method.getDeclaringClass().getSimpleName());
        //8. 反射调用
        method.invoke(beanMap.get(beanName), new Object[]{req, resp, parameterMap.get("tid")[0]});
        // TODO 请求参数、方法参数匹配过程, 请参考 MyDispacherV2Servlet
    }

    private String toFirstChar2Lower(String beanName) {
        char[] chars = beanName.toCharArray();
        chars[0] += 32 ;

        return String.valueOf(chars);
    }
}
