package com.xboot.dynamic.database.modules.invokeapi;

public class TokenKit {
    private volatile String token;

    public synchronized void setToken(String token) {
        this.token = token;
    }

    public synchronized String getToken() {
        while (this.token != null){
            return this.token;
        }
        return null;
    }
}
