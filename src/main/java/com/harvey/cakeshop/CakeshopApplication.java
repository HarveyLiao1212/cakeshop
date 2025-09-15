package com.harvey.cakeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // 啟用 JPA 自動生成 createdDate / lastModifiedDate
public class CakeshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CakeshopApplication.class, args);
    }

}
