package com.xboot.dynamic.database;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.xboot.dynamic.database.modules.system.utils.FileReaderUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class AsyncTaskServiceTest {

    public void executeAsyncTask(Map<String, Object> map, CountDownLatch cdl) {
        long start = System.currentTimeMillis();
        // 导出文件路径
        List<String[]> dataList = null;
        //查询要导出的批次数据
        String fileName = "D:\\temp\\ml-25m\\genome-scores.csv";
        dataList = FileReaderUtil.readCsv(fileName);
        // 写法1
        String filepath = map.get("path").toString() + map.get("page") + ".csv";
        System.out.println(filepath);
        // TestFileUtil.createFile(filepath);

        // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        // EasyExcel.write(filepath, CodeReceptionList.class).sheet("模板").doWrite(dataList);
        OutputStreamWriter is = null;
        ICSVWriter build;
        try {
            is = new OutputStreamWriter(new FileOutputStream(filepath), "utf-8");
            build = new CSVWriterBuilder(is).build();
            build.writeAll(dataList, false);
            build.flushQuietly();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("线程：" + Thread.currentThread().getName() + " , 导出csv   " + map.get("page") + ".csv   成功 , 导出数据 " + dataList.size() + " ,耗时 ：" + (end - start));
        dataList.clear();
        //执行完毕线程数减一
        cdl.countDown();
        System.out.println("剩余任务数  ===========================> " + cdl.getCount());

    }

    public Executor taskExecutor() {
        int i = Runtime.getRuntime().availableProcessors();
        System.out.println("系统最大线程数  ： " + i);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(i);
        taskExecutor.setMaxPoolSize(i);
        taskExecutor.setQueueCapacity(99999);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("csv - taskExecutor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        return taskExecutor;
    }

}
