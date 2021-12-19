package com.xboot.dynamic.database.modules.invokeapi;

import java.util.ArrayList;
import java.util.List;

public class TokenGenerate {
    private static final int MAX_NUM = 10;
    private List<String> tokenCollections = new ArrayList<>();
    private final TokenUtil tokenUtil = new TokenUtil();

    public synchronized void push() {
        while (tokenCollections.size() >= MAX_NUM) {
            try {
                System.out.println("token generate begin ...");
                this.wait();
                System.out.println("token generate end.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        final String token = tokenUtil.newToken();
        tokenCollections.add(token);
        System.out.println(Thread.currentThread().getName() + "生产了一个token, 当前容器的size为：" + tokenCollections.size());
        this.notifyAll();
    }


    public synchronized String pop() {
        while (tokenCollections.size() == 0) {
            try {
                System.out.println("token will be remove begin ...");
                this.wait();
                System.out.println("token will be remove end.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(Thread.currentThread().getName() + "获得了CPU的执行权，准备消费一个token");
        final String tokenRemove = tokenCollections.remove(0);
        System.out.println(Thread.currentThread().getName() + "消费了token为：" + tokenRemove + "  当前的容器的size为：" + tokenCollections.size());
        this.notifyAll();
        return tokenRemove;
    }
}

