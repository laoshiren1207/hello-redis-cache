## 如何不太优雅的使用Redis缓存

我们都知道使用`redis`来缓存我们的数据集合，如下图所示。
![](https://laoshiren.oss-cn-shanghai.aliyuncs.com/412c0d28-10e0-4a29-af48-47ea3787754b.png)

通常自己去缓存数据，这样的优点就是逻辑清晰，而且`redis`的`key`和`value`会比较规范。但是冗余代码会比较多，需要自己进行判断数据是否过期。
为了简化业务代码，现在用注解的方式集成`redis`二级缓存，但是他的`key`和`value`就会比较不符合规范。他的`key`一共包含5个部分，最重要的就是`sql`和这个`sql`的参数。他的`value`就是这个查询的结果集。

### 准备工作

引入依赖，`mybatis`

~~~xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
    <exclusions>
        <!-- 排除 tomcat-jdbc 以使用 HikariCP -->
        <exclusion>
            <groupId>org.apache.tomcat</groupId>
            <artifactId>tomcat-jdbc</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.2.0</version>
</dependency>
~~~

`redis`依赖

~~~xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
~~~

配置文件

~~~yaml
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    # 这里使用的是 ip:3336/db_order 的数据库（一个服务一个数据库）
    url: jdbc:mysql://localhost:3306/fly?useUnicode=true&characterEncoding=utf-8&serverTimezone=Hongkong&useSSL=false
    username: root
    password: 12345678
    hikari:
      minimum-idle: 5
      idle-timeout: 600000
      maximum-pool-size: 10
      auto-commit: true
      pool-name: MyHikariCP
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1
  # redis
  redis:
    host: 120.79.0.210
    port: 6379
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: -1ms
        min-idle: 0
    database: 4
~~~

配置`redisTemplate`

~~~java
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
~~~

注意这里我不使用`String`的序列化方式去序列化`Key`和`Value`

### 实现
实现`Cache`接口

~~~java
package com.laoshiren.hello.redis.cache.mybatis.cache;

import com.laoshiren.hello.redis.cache.mybatis.configure.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ProjectName:     hello-redis-cache
 * Package:         com.laoshiren.hello.redis.cache.mybatis.cache
 * ClassName:       RedisCache
 * Author:          laoshiren
 * Description:
 * Date:            2020/9/13 15:34
 * Version:         1.0
 */
@Slf4j
public class RedisCache implements Cache {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final String id; // cache instance id
    private RedisTemplate redisTemplate;

    private static final long EXPIRE_TIME_IN_MINUTES = 30; // redis过期时间

    public RedisCache(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Put query result to redis
     *
     * @param key
     * @param value
     */
    @Override
    public void putObject(Object key, Object value) {
        try {
            RedisTemplate redisTemplate = getRedisTemplate();
            redisTemplate.opsForValue().set(key, value, EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
            log.debug("Put query result to redis");
        } catch (Throwable t) {
            log.error("Redis put failed", t);
        }
    }

    /**
     * Get cached query result from redis
     *
     * @param key
     * @return
     */
    @Override
    public Object getObject(Object key) {
        try {
            RedisTemplate redisTemplate = getRedisTemplate();
            log.info("Get cached query result from redis");
//            System.out.println("****" + opsForValue.get(key).toString());
            return redisTemplate.opsForValue().get(key);
        } catch (Throwable t) {
            log.error("Redis get failed, fail over to db", t);
            return null;
        }
    }

    /**
     * Remove cached query result from redis
     *
     * @param key
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object removeObject(Object key) {
        try {
            RedisTemplate redisTemplate = getRedisTemplate();
            redisTemplate.delete( key.toString());
            log.debug("Remove cached query result from redis");
        } catch (Throwable t) {
            log.error("Redis remove failed", t);
        }
        return null;
    }

    /**
     * Clears this cache instance
     */
    @Override
    public void clear() {
        RedisTemplate redisTemplate = getRedisTemplate();
        redisTemplate.execute((RedisCallback) connection -> {
            connection.flushDb();
            return null;
        });
        log.debug("Clear all the cached query result from redis");
    }

    /**
     * This method is not used
     *
     * @return
     */
    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    private RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = ApplicationContextHolder.getBean("redisTemplate");
        }
        return redisTemplate;
    }
}
~~~

给指定的`mapper`配置缓存

~~~java
@CacheNamespace(implementation = RedisCache.class)
public interface TbPostMapper extends BaseMapper<TbPost> {
}
~~~

### 测试
请求一次数据库，使用`Debug`模式，它的`key`是一个`CacheKey`，无法使用使用`StringRedisSerializer`去序列化，所以**`redisTemplate`得使用默认的序列化，即`JdkSerializationRedisSerializer`**
![](https://laoshiren.oss-cn-shanghai.aliyuncs.com/e63f2a70-e7be-462b-9c45-c2582148910f.png)

打开`RDM`，看一下4号库。

![](https://laoshiren.oss-cn-shanghai.aliyuncs.com/f54ec9ac-e54f-485e-8aef-9af7b57d071e.png)

发现`key`和`value`就不是很美观，不过不影响使用，当然您可以使用`StringRedisSerializer`去实现，只不过我在尝试的过程中获取`sql`和参数的时候，会出现一点问题。希望有大佬可以指出。

### 带参数的sql

![](https://laoshiren.oss-cn-shanghai.aliyuncs.com/ecefc2cb-a43b-4af7-8583-8d5ae2c639ef.png)

**特别注意！**在分页缓存的时候，`Page`对象的`total`必须自己手动查询一次，不然返回给前端的对象里第一次还有总页数，第二次由于走了缓存就不带这个`total`，所以必须手动查询一次。

~~~java
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
~~~



后续我也会继续更新这篇博客，特别在序列化的方式上。好了，最后还是借用大佬的一句话：“不经一番寒彻骨，怎知梅花扑鼻香”。