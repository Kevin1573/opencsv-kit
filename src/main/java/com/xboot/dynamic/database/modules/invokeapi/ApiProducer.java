package com.xboot.dynamic.database.modules.invokeapi;

import java.util.concurrent.*;

public class ApiProducer extends Thread {

    private TokenGenerate tokenGenerate;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public ApiProducer(String name, TokenGenerate tokenGenerate) {
        this.tokenGenerate = tokenGenerate;
        this.setName(name);
    }

    @Override
    public void run() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                tokenGenerate.push();
            }
        },0, 10, TimeUnit.SECONDS);
    }
}
