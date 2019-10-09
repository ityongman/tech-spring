package com.ityongman.dateutils;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Date;

public class TestDateUtils {

    @Test
    public void testDateConvert() {
        long newTime = DateUtils.addMonths(new Date(2561163055630L), 365).getTime();
        System.out.println(newTime);
    }
}
