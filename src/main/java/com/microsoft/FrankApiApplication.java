package com.microsoft;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.registry.zookeeper.ZookeeperInstance;
import org.apache.dubbo.remoting.zookeeper.curator5.ZookeeperClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.microsoft.mapper")
@EnableDubbo
public class FrankApiApplication {

    public static void main(String[] args) {
        System.setProperty("nacos.logging.default.config.enabled", "false");
        SpringApplication.run(FrankApiApplication.class, args);
    }

}
