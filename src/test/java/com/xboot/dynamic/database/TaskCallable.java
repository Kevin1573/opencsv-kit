package com.xboot.dynamic.database;

import java.util.concurrent.Callable;

public class TaskCallable implements Callable {
    @Override
    public Object call() throws Exception {
        Object[] objects = new Object[2];
        objects[0] = "Hello World!";
        objects[1] = "Helloboy";
        return objects;
    }
}
