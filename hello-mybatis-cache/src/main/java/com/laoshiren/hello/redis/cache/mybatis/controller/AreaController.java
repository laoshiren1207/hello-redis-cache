package com.laoshiren.hello.redis.cache.mybatis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laoshiren.hello.redis.cache.mybatis.domain.Area;
import com.laoshiren.hello.redis.cache.mybatis.domain.CodeStatus;
import com.laoshiren.hello.redis.cache.mybatis.domain.ResponseResult;

import com.laoshiren.hello.redis.cache.mybatis.service.IAreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * ProjectName:     hello-redis-cache
 * Package:         com.laoshiren.hello.redis.cache.mybatis.controller
 * ClassName:       AreaController
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:
 * Date:            2020/9/20 17:56
 * Version:         1.0.0
 */
@RestController
@RequestMapping("area")
public class AreaController {

    @Resource
    private IAreaService areaService;

    @GetMapping("page/{pageNo}/{pageSize}")
    public ResponseResult<IPage<Area>> page(@PathVariable(name = "pageNo")Integer pageNo,
                                            @PathVariable(name = "pageSize") Integer pageSize,
                                            HttpServletRequest request){
        IPage<Area> wherePage = new Page<>(pageNo, pageSize);
        String word = request.getParameter("wd");
        LambdaQueryWrapper<Area> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(word)) {
            queryWrapper.like(Area::getAreaName,word);
        }
        int count = areaService.count(queryWrapper);
        IPage<Area> page = areaService.page(wherePage,queryWrapper);
        page.setTotal((long)count);
        return new ResponseResult<>(CodeStatus.OK,"操作成功",page);
    }


}
