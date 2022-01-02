package com.xboot.dynamic.database.modules.invokeapi;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
public class TokenDelayDto implements Delayed {
    // token
    private String token;
    // 过期时间
    private long expirationTimes;

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return unit.convert(this.expirationTimes - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        final TokenDelayDto delayDto = (TokenDelayDto) o;

        return this.expirationTimes == delayDto.getExpirationTimes() ?
                0 : (delayDto.expirationTimes < this.expirationTimes ?
                1 : -1);
    }
}
