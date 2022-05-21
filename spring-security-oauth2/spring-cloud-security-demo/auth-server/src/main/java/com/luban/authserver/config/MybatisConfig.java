package com.luban.authserver.config;

import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Fox
 */
@Configuration
@MapperScan("com.luban.authserver.mapper")
public class MybatisConfig {

//    @ConfigurationProperties("spring.datasource")
//    @Bean
//    public DataSource dataSource() {
//        return new HikariDataSource();
//    }
}
