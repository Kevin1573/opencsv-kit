package com.xboot.dynamic.database.modules.invokeapi;

public class TokenUtilTest {
    public static void main(String[] args) {
        final TokenUtil tokenUtil = new TokenUtil();
        while (true){
            final String s = tokenUtil.newToken();
            System.out.println("主线程获取： "+s);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {w
//                e.printStackTrace();
//            }
        }
    }
}
