package com.ityongman.framework.bean.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author shedunze
 * @Date 2020-01-03 11:46
 * @Description 加载外部资源的类
 * spring 中体现的是XmlBeanDefinitionReader.loadBeanDefinitions 加载资源
 * 这里为了方便用 properties 来解析
 */
public class BeanDefinitionReader {
    /**
     * 已经注册的类
     */
    private List<String> registyBeanClasses = new ArrayList<>() ;
    /**
     * 配置熟悉
     * 为了实验方便, 这里使用Properties存储解析的内容
     */
    private Properties config = new Properties() ;
    /**
     * 解析的配置属性
     * 配置文件中需要固定的参数
     */
    private final static String SCAN_PACKAGE = "scanPackage" ;

    public BeanDefinitionReader(String... locations) {
        //1. 加载配置文件为流信息
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0])){
            //2. 解析信息到Properties中
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //3. 扫描scanPackage信息
        doScanner(config.getProperty(SCAN_PACKAGE)) ;
    }

    //扫描配置信息
    private void doScanner(String scanPath) {

    }
}
