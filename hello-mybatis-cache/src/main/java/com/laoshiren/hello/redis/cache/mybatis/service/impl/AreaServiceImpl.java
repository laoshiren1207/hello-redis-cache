package com.laoshiren.hello.redis.cache.mybatis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.laoshiren.hello.redis.cache.mybatis.domain.Area;
import com.laoshiren.hello.redis.cache.mybatis.mapper.AreaMapper;
import com.laoshiren.hello.redis.cache.mybatis.service.IAreaService;
import org.springframework.stereotype.Service;


/**
 * ProjectName:     hello-redis-cache
 * Package:         com.laoshiren.hello.redis.cache.mybatis.service.impl
 * ClassName:       IAreaServiceImpl
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:
 * Date:            2020/9/20 17:51
 * Version:         1.0.0
 */
@Service(value = "areaService")
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {

}
