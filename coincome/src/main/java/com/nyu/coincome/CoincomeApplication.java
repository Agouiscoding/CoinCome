package com.nyu.coincome;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;


@SpringBootApplication
@MapperScan("com.nyu.coincome.mapper")
public class CoincomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoincomeApplication.class, args);

    }
}

