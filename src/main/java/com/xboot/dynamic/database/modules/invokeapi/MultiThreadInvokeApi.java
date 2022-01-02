package com.xboot.dynamic.database.modules.invokeapi;

import cn.hutool.http.HttpUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadInvokeApi {

    private AtomicInteger apiInvokeNum = new AtomicInteger(0);
    private volatile String globalToken;

    public MultiThreadInvokeApi() {
    }

    public void invoke() throws Exception {

//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        Request request = new Request.Builder()
//                .url("http://api.qingyunke.com/api.php?key=free&appid=0&msg=关键词")
//                .method("GET", null)
//                .build();
//        Response response = client.newCall(request).execute();
//        System.out.println(response.body());

        // 最简单的HTTP请求，可以自动通过header等信息判断编码，不区分HTTP和HTTPS
        String result1 = HttpUtil.get("http://localhost:8080/system/user/");
        System.out.println(result1);
        System.out.println("接口被调用了：" + apiInvokeNum.getAndIncrement() + "次, 使用的token为：" + this.globalToken);
    }

    public void setGlobalToken(String globalToken) {
        this.globalToken = globalToken;
    }
}
