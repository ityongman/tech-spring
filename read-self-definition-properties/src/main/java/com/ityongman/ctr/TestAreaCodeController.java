package com.ityongman.ctr;

import com.ityongman.config.AreaCodeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAreaCodeController {
    @Autowired
    private AreaCodeConfig areaCodeConfig;

    @RequestMapping("/query/codes")
    public String queryAreaCodes() {
        return areaCodeConfig.areaCodes.toString();
    }
}
