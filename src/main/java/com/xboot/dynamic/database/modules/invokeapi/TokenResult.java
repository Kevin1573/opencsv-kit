package com.xboot.dynamic.database.modules.invokeapi;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class TokenResult implements Delayed {
    private String name;
    private long executeTime; // 延时时长

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return 0;
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
}
