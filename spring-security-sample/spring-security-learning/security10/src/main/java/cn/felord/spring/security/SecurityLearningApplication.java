package cn.felord.spring.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author dax
 */
@MapperScan({"cn.felord.spring.security.mapper*"})
@EnableCaching
@SpringBootApplication
public class SecurityLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityLearningApplication.class, args);
    }

}
