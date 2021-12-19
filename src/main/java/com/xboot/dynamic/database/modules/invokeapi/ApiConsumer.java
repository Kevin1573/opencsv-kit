package com.xboot.dynamic.database.modules.invokeapi;

public class ApiConsumer extends Thread {

    private TokenGenerate tokenGenerate;
    private final MultiThreadInvokeApi multiThreadInvokeApi;
    //volatile修饰符用来保证其它线程读取的总是该变量的最新的值
    public volatile boolean exit = false;

    public ApiConsumer(String name, TokenGenerate tokenGenerate, MultiThreadInvokeApi multiThreadInvokeApi) {
        this.setName(name);
        this.tokenGenerate = tokenGenerate;
        this.multiThreadInvokeApi = multiThreadInvokeApi;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                final String token = this.tokenGenerate.pop();
                this.multiThreadInvokeApi.setGlobalToken(token);
                this.multiThreadInvokeApi.invoke();
            } catch (Exception e) {
                System.out.println("================" + Thread.currentThread().getName() + "======方法退出============");
                this.exit = true;
            }
        }
    }
}
