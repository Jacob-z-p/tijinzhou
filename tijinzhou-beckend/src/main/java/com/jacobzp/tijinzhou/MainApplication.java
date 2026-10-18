package com.jacobzp.tijinzhou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.session.data.redis.autoconfigure.SessionDataRedisAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
// Redis 只给签到 Bitmap 用。排除 Spring Session，登录态继续放在容器 Session
@SpringBootApplication(exclude = {SessionDataRedisAutoConfiguration.class})
@MapperScan("com.jacobzp.tijinzhou.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
