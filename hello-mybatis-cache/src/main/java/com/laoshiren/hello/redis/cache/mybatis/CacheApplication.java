package com.laoshiren.hello.redis.cache.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ProjectName:     hello-redis-cache
 * Package:         com.laoshiren.hello.redis.cache.mybatis
 * ClassName:       CacheApplication
 * Author:          laoshiren
 * Description:
 * Date:            2020/9/13 15:34
 * Version:         1.0
 */
@SpringBootApplication(scanBasePackages = "com.laoshiren.hello.redis.cache.mybatis")
@MapperScan(basePackages = "com.laoshiren.hello.redis.cache.mybatis.mapper")
public class CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class,args);
    }

}
