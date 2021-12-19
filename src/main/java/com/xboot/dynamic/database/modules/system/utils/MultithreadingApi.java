package com.xboot.dynamic.database.modules.system.utils;

import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ConcurrencyTester;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.ArrayList;
import java.util.concurrent.*;

public class MultithreadingApi {

    public static void main(String[] args) {
        //获取当前系统cpu的数目
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors);

//        try {
//            final ArrayList<Future> resultList = new ArrayList<>();
//            for (int i = 0; i < 100; i++) {
//                final Future<String> submit = fixedThreadPool.submit(new ApiThread());
//                resultList.add(submit);
//            }
//            for (Future future : resultList) {
//                System.out.println(future.get());
//            }
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        CompletableFuture.supplyAsync(()->{

            return null;
        }, fixedThreadPool);
        fixedThreadPool.shutdown();

        invokeApi();
    }

    private static void invokeApi() {
        final String token = getToken();
        ConcurrencyTester tester = ThreadUtil.concurrencyTest(100, () -> {
            // 测试的逻辑内容
            long delay = RandomUtil.randomLong(100, 1000);
            ThreadUtil.sleep(delay);
            Console.log("{} test finished, delay: {}", Thread.currentThread().getName(), delay);
        });

            // 获取总的执行时间，单位毫秒
        Console.log(tester.getInterval());

    }
    private static String getToken() {
        return "token";
    }
}
