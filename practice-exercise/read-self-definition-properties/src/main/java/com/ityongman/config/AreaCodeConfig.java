package com.ityongman.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class AreaCodeConfig {
    public static Map<String, List<String>> areaCodes = new HashMap<>();

	public Map<String, List<String>> getAreaCodes(Resource res) throws IOException {

        List<String> lines = Files.readAllLines(Paths.get(res.getURI()),
                StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] words = line.split("=");

            if (null != words && words.length ==2) {
            	
            	if(!areaCodes.containsKey(words[0])) {
            		List<String> codes = new ArrayList<String>();
            		codes.add(words[1]);
            		areaCodes.put(words[0], codes);
            	} else {
            		List<String> codes = areaCodes.get(words[0]);
            		codes.add(words[1]);
            	}
            }
        }
        return areaCodes;
    }
}
