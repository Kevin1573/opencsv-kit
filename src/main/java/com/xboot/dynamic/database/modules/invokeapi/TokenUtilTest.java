package com.xboot.dynamic.database.modules.invokeapi;

import cn.hutool.core.lang.generator.UUIDGenerator;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTHeader;
import cn.hutool.jwt.JWTUtil;

import java.util.HashMap;
import java.util.Map;

public class TokenUtilTest {
    public static void main(String[] args) {
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
        System.out.println(token);

        final JWT jwt = JWTUtil.parseToken(token);

        jwt.getHeader(JWTHeader.TYPE);
        jwt.getPayload("sub");

        String tokenOld = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiIwYzU3MmI5Ni1hNDY3LTRlYTUtODZmMy1mZTVmMjJkOTgxODQiLCJleHBpcmVfdGltZSI6MTYzOTk3ODMzNjgyNn0.GSaUM275YwZnccTssgbm8TNlKmby_dj-SJ_SPproYlo";
        final boolean verify = JWTUtil.verify(tokenOld, "123456".getBytes());
        System.out.println(verify);
    }

    public static void main2(String[] args) {
        final TokenUtil tokenUtil = new TokenUtil();
        while (true){
            final String s = tokenUtil.newToken();
            System.out.println("主线程获取： "+s);
        }
    }
}
