package com.spring.security.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * @author security
 */
@Configuration
@ComponentScan(basePackages = "com.spring.security",excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,value = Controller.class)})
//@MapperScan("com.spring.session.mapper")
public class AppConfig {

//    @Bean
//    public DataSource dataSource(){
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        dataSource.setUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf8&useSSL=false&serverTimezone=UTC");
//        dataSource.setUsername("root");
//        dataSource.setPassword("123456");
//        return dataSource;
//    }

//    @Bean
//    public SqlSessionFactory sqlSessionFactory() throws Exception {
//        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
//        factoryBean.setDataSource(dataSource());
//        return factoryBean.getObject();
//    }

}
