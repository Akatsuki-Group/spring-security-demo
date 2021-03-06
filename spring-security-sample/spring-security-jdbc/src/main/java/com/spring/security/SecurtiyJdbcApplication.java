package com.spring.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.spring.security.mapper")
public class SecurtiyJdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurtiyJdbcApplication.class, args);
    }

}
