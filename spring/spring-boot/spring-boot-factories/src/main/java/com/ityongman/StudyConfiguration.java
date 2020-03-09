package com.ityongman;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shedunze
 * @Date 2020-03-09 14:17
 * @Description
 */
@Configuration
public class StudyConfiguration {

    @Bean
    public StudyClass studyClass() {
        return new StudyClass() ;
    }
}
