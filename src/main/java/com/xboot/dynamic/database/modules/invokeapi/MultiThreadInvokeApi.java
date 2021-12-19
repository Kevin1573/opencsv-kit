package com.xboot.dynamic.database.modules.invokeapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadInvokeApi {

    private AtomicInteger apiInvokeNum = new AtomicInteger(0);
    private volatile String globalToken;

    public MultiThreadInvokeApi() {
    }

    public void invoke() throws Exception {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://api.qingyunke.com/api.php?key=free&appid=0&msg=关键词")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body());
        System.out.println("接口被调用了：" + apiInvokeNum.getAndIncrement() + "次, 使用的token为：" + this.globalToken);
    }

    public void setGlobalToken(String globalToken) {
        this.globalToken = globalToken;
    }
}
