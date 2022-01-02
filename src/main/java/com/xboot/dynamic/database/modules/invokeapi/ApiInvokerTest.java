package com.xboot.dynamic.database.modules.invokeapi;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiInvokerTest {

    public static void main(String[] args) {
        int times = 15000;
//        syncInvoke(times);
//        asyncInvoke(times);

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        final TokenUtil tokenUtil = new TokenUtil();
        final TokenKit tokenKit = new TokenKit();

        // 5s 获取一次token
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            tokenKit.setToken(tokenUtil.genJwtToken());
        }, 0, 5, TimeUnit.SECONDS);

        new Thread(new Runnable() {
            private volatile boolean exit = false;

            @Override
            public void run() {
                final File file = new File("d:/apiInvoke01.log");
                try (FileWriter fileWriter = new FileWriter(file)) {
                    while (!exit) {
                        String token = tokenKit.getToken();
                        while (token == null) {
                            token = tokenKit.getToken();
                        }
                        fileWriter.write(token + "\t\n");
                        String apiUri = "http://localhost:8080/system/user/";
                        String result = HttpUtil.get(apiUri);
                        fileWriter.write(result + "\t\n");
                        fileWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    this.exit = true;
                }
            }
        }).start();

        new Thread(new Runnable() {
            private volatile boolean exit = false;

            @Override
            public void run() {
                final File file = new File("d:/apiInvoke02.log");
                try (FileWriter fileWriter = new FileWriter(file)) {
                    while (!exit) {
                        String token = tokenKit.getToken();
                        while (token == null) {
                            token = tokenKit.getToken();
                        }
                        fileWriter.write(token + "\t\n");
                        String apiUri = "http://localhost:8080/system/user/some";
                        String result = HttpUtil.get(apiUri);
                        fileWriter.write(result + "\t\n");
                        fileWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    this.exit = true;
                }
            }
        }).start();

        new Thread(new Runnable() {
            private AtomicInteger errors = new AtomicInteger(0);

            @Override
            public void run() {
                final File file = new File("d:/apiInvoke03.log");
                try (FileWriter fileWriter = new FileWriter(file)) {
                    while (!Thread.currentThread().isInterrupted()) {
                        String token = tokenKit.getToken();
                        while (token == null) {
                            token = tokenKit.getToken();
                        }
                        fileWriter.write(token + "\t\n");
                        try {

                            String apiUri = "http://localhost:8080/system/user/error";
                            final HttpResponse response = HttpRequest.get(apiUri).execute();
                            fileWriter.write(response.getStatus() + "\t ");
                            if (response.isOk()) {
                                fileWriter.write(response.body() + "\t\n");
                            } else {
                                if (errors.incrementAndGet() > 5) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        } catch (RuntimeException re) {
                            //中断状态在抛出异常前，被清除掉，因此在此处重置中断状态
                            Thread.currentThread().interrupt();
                        }

                        fileWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("start ... ");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }

    private static CopyOnWriteArrayList<String> tokenCollections = new CopyOnWriteArrayList<>();

    private static void syncInvoke(int times) {
        final long l = System.currentTimeMillis();

        for (int i = 0; i < times; i++) {
            final String oldToken = tokenCollections.remove(0);
            String apiUri = "http://localhost:8080/system/user/";
            String result = HttpUtil.get(apiUri);
            //System.out.println(result);
        }

        final long re = System.currentTimeMillis() - l;
        System.out.println("共消耗：" + re);
    }

    private static void asyncInvoke(int times) {
        final long l = System.currentTimeMillis();
        List<Future> futureTask = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            final CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
                String apiUri = "http://localhost:8080/system/user/";
                String result = HttpUtil.get(apiUri);
                return result;
            });
            futureTask.add(supplyAsync);
        }

        //循环遍历结果
        futureTask.forEach(c -> {
            try {
                String r = (String) c.get();
                //System.out.println(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        final long re = System.currentTimeMillis() - l;
        System.out.println("共消耗：" + re);
    }
}
