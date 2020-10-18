package com.laoshiren.hello.redis.cache.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laoshiren.hello.redis.cache.mybatis.cache.RedisCache;
import com.laoshiren.hello.redis.cache.mybatis.domain.Area;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

/**
 * ProjectName:     hello-redis-cache 
 * Package:         com.laoshiren.hello.redis.cache.mybatis.mapper
 * ClassName:       AreaMapper
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     ${description}  
 * Date:            2020/9/20 17:48
 * Version:         1.0.0
 */
@CacheNamespace(implementation = RedisCache.class)
@Mapper
public interface AreaMapper extends BaseMapper<Area> {
}