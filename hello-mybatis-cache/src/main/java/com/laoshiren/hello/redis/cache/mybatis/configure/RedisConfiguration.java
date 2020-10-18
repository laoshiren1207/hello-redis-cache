package com.laoshiren.hello.redis.cache.mybatis.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * ProjectName:     hello-redis-cache
 * Package:         com.laoshiren.hello.redis.cache.mybatis.configure
 * ClassName:       RedisConfiguration
 * Author:          laoshiren
 * Description:
 * Date:            2020/9/13 15:49
 * Version:         1.0
 */
@Configuration
public class RedisConfiguration {


    /**
     * 设置redisTemplate
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 使用String 序列化
//        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


}
