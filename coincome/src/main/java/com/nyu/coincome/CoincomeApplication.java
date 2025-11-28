package com.nyu.coincome;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import java.util.TimeZone;
import org.springframework.jdbc.core.JdbcTemplate;


@SpringBootApplication
@EnableScheduling
@MapperScan("com.nyu.coincome.mapper")
public class CoincomeApplication {
    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    }

    public static void main(String[] args) {
        SpringApplication.run(CoincomeApplication.class, args);

    }
}

