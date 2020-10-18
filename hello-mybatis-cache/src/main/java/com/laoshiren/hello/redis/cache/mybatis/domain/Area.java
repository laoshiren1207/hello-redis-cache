package com.laoshiren.hello.redis.cache.mybatis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * ProjectName:     hello-redis-cache 
 * Package:         com.laoshiren.hello.redis.cache.mybatis.domain
 * ClassName:       Area
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     ${description}  
 * Date:            2020/9/20 17:48
 * Version:         1.0.0
 */
/**
    * 地区设置
    */
@Data
@TableName(value = "area")
public class Area implements Serializable {
    /**
     * 自增列
     */
    @TableId(value = "area_id", type = IdType.AUTO)
    private Integer areaId;

    /**
     * 区代码
     */
    @TableField(value = "area_code")
    private String areaCode;

    /**
     * 父级市代码
     */
    @TableField(value = "city_code")
    private String cityCode;

    /**
     * 市名称
     */
    @TableField(value = "area_name")
    private String areaName;

    /**
     * 简称
     */
    @TableField(value = "short_name")
    private String shortName;

    /**
     * 经度
     */
    @TableField(value = "lng")
    private String lng;

    /**
     * 纬度
     */
    @TableField(value = "lat")
    private String lat;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 创建用户id
     */
    @TableField(value = "create_user")
    private Integer createUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_date")
    private Date createDate;

    /**
     * 更新用户id
     */
    @TableField(value = "modify_user")
    private Integer modifyUser;

    /**
     * 修改时间
     */
    @TableField(value = "modify_date")
    private Date modifyDate;

    /**
     * 备注
     */
    @TableField(value = "memo")
    private String memo;

    /**
     * 状态
     */
    @TableField(value = "data_state")
    private Integer dataState;

    private static final long serialVersionUID = 1L;

    public static final String COL_AREA_ID = "area_id";

    public static final String COL_AREA_CODE = "area_code";

    public static final String COL_CITY_CODE = "city_code";

    public static final String COL_AREA_NAME = "area_name";

    public static final String COL_SHORT_NAME = "short_name";

    public static final String COL_LNG = "lng";

    public static final String COL_LAT = "lat";

    public static final String COL_SORT = "sort";

    public static final String COL_CREATE_USER = "create_user";

    public static final String COL_CREATE_DATE = "create_date";

    public static final String COL_MODIFY_USER = "modify_user";

    public static final String COL_MODIFY_DATE = "modify_date";

    public static final String COL_MEMO = "memo";

    public static final String COL_DATA_STATE = "data_state";
}