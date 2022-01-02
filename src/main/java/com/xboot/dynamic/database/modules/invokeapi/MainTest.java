package com.xboot.dynamic.database.modules.invokeapi;

public class MainTest {

    public static void main(String[] args) {
        final TokenGenerate tokenGenerate = new TokenGenerate();
        final MultiThreadInvokeApi multiThreadInvokeApi = new MultiThreadInvokeApi();
        for (int i = 0; i < 20; i++) {
            final ApiProducer apiProducer = new ApiProducer("生产者"+i, tokenGenerate);
            apiProducer.start();
        }

        for (int i = 0; i < 20; i++) {
            final ApiConsumer apiConsumer = new ApiConsumer("消费者"+i, tokenGenerate, multiThreadInvokeApi);
            apiConsumer.start();
        }

    }
}
