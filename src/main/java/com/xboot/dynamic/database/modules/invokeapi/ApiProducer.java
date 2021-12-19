package com.xboot.dynamic.database.modules.invokeapi;

public class ApiProducer extends Thread {

    private TokenGenerate tokenGenerate;

    public ApiProducer(String name, TokenGenerate tokenGenerate) {
        this.tokenGenerate = tokenGenerate;
        this.setName(name);
    }

    @Override
    public void run() {
        while (true){
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            this.tokenGenerate.push();
        }
    }
}
