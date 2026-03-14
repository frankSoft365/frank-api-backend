package com.microsoft;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.microsoft.mapper")
public class FrankApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrankApiApplication.class, args);
    }

}
