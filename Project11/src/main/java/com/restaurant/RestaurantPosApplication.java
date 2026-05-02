package com.restaurant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.restaurant.mapper")
public class RestaurantPosApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantPosApplication.class, args);
    }
}
