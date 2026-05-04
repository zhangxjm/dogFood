package com.homemaking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.homemaking.mapper")
public class HomemakingApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomemakingApplication.class, args);
    }
}
