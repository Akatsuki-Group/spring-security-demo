package cn.felord.spring.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author dax
 */
@MapperScan({"cn.felord.spring.security.mapper*"})
@EnableCaching
@SpringBootApplication
public class SecurityLearningApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(SecurityLearningApplication.class);

        springApplication.addListeners(new ApplicationPidFileWriter());
        springApplication.run(args);
    }

}
