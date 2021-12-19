package com.xboot.dynamic.database.modules.invokeapi;

public class MainTest {

    public static void main(String[] args) {
        final TokenGenerate tokenGenerate = new TokenGenerate();
        final MultiThreadInvokeApi multiThreadInvokeApi = new MultiThreadInvokeApi();
        final ApiProducer apiProducer1 = new ApiProducer("生产者1", tokenGenerate);
        final ApiProducer apiProducer2 = new ApiProducer("生产者2", tokenGenerate);
        final ApiProducer apiProducer3 = new ApiProducer("生产者3", tokenGenerate);
        final ApiConsumer apiConsumer1 = new ApiConsumer("消费者1", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer2 = new ApiConsumer("消费者2", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer3 = new ApiConsumer("消费者3", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer4 = new ApiConsumer("消费者4", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer5 = new ApiConsumer("消费者5", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer6 = new ApiConsumer("消费者6", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer7 = new ApiConsumer("消费者7", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer8 = new ApiConsumer("消费者8", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer9 = new ApiConsumer("消费者9", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer10 = new ApiConsumer("消费者10", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer11 = new ApiConsumer("消费者11", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer12 = new ApiConsumer("消费者12", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer13 = new ApiConsumer("消费者13", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer14 = new ApiConsumer("消费者14", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer15 = new ApiConsumer("消费者15", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer16 = new ApiConsumer("消费者16", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer17 = new ApiConsumer("消费者17", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer18 = new ApiConsumer("消费者18", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer19 = new ApiConsumer("消费者19", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer20 = new ApiConsumer("消费者20", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer21 = new ApiConsumer("消费者21", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer22 = new ApiConsumer("消费者22", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer23 = new ApiConsumer("消费者23", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer24 = new ApiConsumer("消费者24", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer25 = new ApiConsumer("消费者25", tokenGenerate, multiThreadInvokeApi);
        final ApiConsumer apiConsumer26 = new ApiConsumer("消费者26", tokenGenerate, multiThreadInvokeApi);

        apiProducer1.start();
        apiProducer2.start();
        apiProducer3.start();
        apiConsumer1.start();
        apiConsumer2.start();
        apiConsumer3.start();
        apiConsumer4.start();
        apiConsumer5.start();
        apiConsumer6.start();
        apiConsumer7.start();
        apiConsumer8.start();
        apiConsumer9.start();
        apiConsumer10.start();
        apiConsumer11.start();
        apiConsumer12.start();
        apiConsumer13.start();
        apiConsumer14.start();
        apiConsumer15.start();
        apiConsumer16.start();
        apiConsumer17.start();
        apiConsumer18.start();
        apiConsumer19.start();
        apiConsumer20.start();
        apiConsumer21.start();
        apiConsumer22.start();
        apiConsumer23.start();
        apiConsumer24.start();
        apiConsumer25.start();
        apiConsumer26.start();
    }
}
