package com.inventory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.inventory.mapper")
public class InventorySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventorySystemApplication.class, args);
    }

}
