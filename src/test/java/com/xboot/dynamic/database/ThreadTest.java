package com.xboot.dynamic.database;

public class ThreadTest {
    private static int count;
    private static boolean mark = true ;
    public static void main(String[] args) throws InterruptedException {
        final Thread thread = new Thread(() -> {
            while(mark){
                System.out.println(Thread.currentThread().getName()+"正在运行~");
                try {
                    Thread.currentThread().sleep(2_500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    mark = false ;
                    System.err.println(Thread.currentThread().getName()+"接收到中断信号");
                }
            }
        }, "测试线程");
        thread.start();
        Thread.currentThread().sleep(1_000);
        thread.interrupt();
    }
}
