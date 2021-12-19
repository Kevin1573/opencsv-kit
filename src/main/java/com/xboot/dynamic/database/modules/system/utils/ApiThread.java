package com.xboot.dynamic.database.modules.system.utils;

import java.util.concurrent.Callable;

public class ApiThread implements Callable<String> {
    @Override
    public String call() throws Exception {

        return "api 调用成功";
    }
}
