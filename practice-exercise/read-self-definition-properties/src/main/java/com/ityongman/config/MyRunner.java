package com.ityongman.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MyRunner implements CommandLineRunner {
    @Value("${near.city}")
    private String nearCityPath ;

    @Value("${area.code}")
    private String areaCodePath ;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private CountWords countWords;

    @Autowired
    private AreaCodeConfig areaCodeConfig;

    @Override
    public void run(String... args) throws Exception {
        Resource res = resourceLoader.getResource(nearCityPath /*"classpath:thermopylae.txt"*/);
        Map<String, List<String>> nearCitys = countWords.getNearCity(res);
        for (String key : nearCitys.keySet()) {
            System.out.println(key + ": " + nearCitys.get(key));
        }

        res = resourceLoader.getResource(areaCodePath /*"classpath:usareacode.properties"*/);
        Map<String, List<String>> areaCodes = areaCodeConfig.getAreaCodes(res);
        for (String key : areaCodes.keySet()) {
            System.out.println("--> " + key + ": " + areaCodes.get(key));
        }
    }
}
