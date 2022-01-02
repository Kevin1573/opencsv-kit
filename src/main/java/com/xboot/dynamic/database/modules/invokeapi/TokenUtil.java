package com.xboot.dynamic.database.modules.invokeapi;

import cn.hutool.core.lang.generator.UUIDGenerator;
import cn.hutool.jwt.JWTUtil;
import com.google.common.util.concurrent.RateLimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenUtil {
    //每秒产生5个令牌， 一个令牌就要0.2秒
    // qps 每秒钟请求数量
    // permitsPerSecond = 1 / qps
    private static RateLimiter rateLimiter = RateLimiter.create(0.05);
    private static String token = null;
    private volatile boolean isFirst = false;
    private AtomicInteger adder = new AtomicInteger(1);

    public String newToken() {

        if (!isFirst && token == null) {
            token = getToken();
            isFirst = true;
        }

        while (rateLimiter.tryAcquire()) {
            adder = new AtomicInteger(1);
            token = getToken();
        }
        return token + " - " + adder.incrementAndGet();

    }

    public String genJwtToken() {
        final UUIDGenerator uuidGenerator = new UUIDGenerator();
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {

                put("uid", uuidGenerator.next());
                long timestamp15d = 1000 * 60 * 60 * 24 * 15; //15天
                long timestamp1d = 1000 * 60 * 60 * 24; //1天
                long timestamp1h = 1000 * 60 * 60 * 1; //1小时
                long timestamp1s = 1000 * 60 * 1; //1分钟

                put("expire_time", System.currentTimeMillis() + timestamp1s);
            }
        };

        final String token = JWTUtil.createToken(map, "123456".getBytes());
        return token;
    }

    private static String getToken() {
        System.out.println("获得一个新的token");
        return new UUIDGenerator().next();
    }
}
