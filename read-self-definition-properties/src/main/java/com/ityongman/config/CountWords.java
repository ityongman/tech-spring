package com.ityongman.config;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Component
public class CountWords {
    private static Map<String, List<String>> nearCitys = new HashMap<>();

    public Map<String, List<String>> getNearCity(Resource res) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(res.getURI()),
                StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] words = line.split("\t");

            if (null != words && words.length ==2) {
                String[] nearCityArr = words[1].split("/");
                List<String> nearCity = Arrays.asList(nearCityArr);
                if(!nearCitys.containsKey(words[0])) {
                    nearCitys.put(words[0], nearCity);
                }
            }
        }
        return nearCitys;
    }

}
