package com.jacobzp.tijinzhou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
// 默认排除 Redis，登录态放在容器 Session。开启分布式 Session 时配置 spring.data.redis，并移除此 exclude
@SpringBootApplication(exclude = {DataRedisAutoConfiguration.class})
@MapperScan("com.jacobzp.tijinzhou.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
