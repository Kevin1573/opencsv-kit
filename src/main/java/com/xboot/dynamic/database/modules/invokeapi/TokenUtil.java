package com.xboot.dynamic.database.modules.invokeapi;

import cn.hutool.core.lang.generator.UUIDGenerator;
import com.google.common.util.concurrent.RateLimiter;

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

    private static String getToken() {
        System.out.println("获得一个新的token");
        return new UUIDGenerator().next();
    }
}
