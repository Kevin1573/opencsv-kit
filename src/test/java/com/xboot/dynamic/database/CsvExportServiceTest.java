package com.xboot.dynamic.database;

import com.xboot.dynamic.database.modules.system.utils.ZipUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class CsvExportServiceTest {
    /**
     * 每批次处理的数据量
     */
    private static final int LIMIT = 50;

    public static Queue<Map<String, Object>> queue;//Queue是java自己的队列，具体可看API，是同步安全的

    static {
        queue = new ConcurrentLinkedQueue<Map<String, Object>>();
    }

    private String filePath = "D:/temp/";
    //    @Resource
    private AsyncTaskServiceTest asyncTaskService;

    public CsvExportServiceTest(AsyncTaskServiceTest asyncTaskService) {
        this.asyncTaskService = asyncTaskService;
    }

    public static void main(String[] args) {
        AsyncTaskServiceTest asyncTask = new AsyncTaskServiceTest();
        CsvExportServiceTest serviceTest = new CsvExportServiceTest(asyncTask);
        try {
            serviceTest.threadCsv(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化队列
     */
    public void initQueue() {
        // 设置数据
        //long count = codeReceptionListDao.count(new HashMap<>());
        long listCount = 500;
        int listCount1 = (int) listCount;
        String randomDirName = UUID.randomUUID().toString().replaceAll("-", "");

        if (!filePath.endsWith("/")) {
            filePath += File.separator;
        }
        filePath = filePath + randomDirName;
        System.out.println(filePath);
        File tempDir = new File(filePath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        //导出6万以上数据。。。
        int count = listCount1 / LIMIT + (listCount1 % LIMIT > 0 ? 1 : 0);//循环次数
        for (int i = 1; i <= count; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("page", i);
            map.put("limit", LIMIT);
            map.put("path", filePath + File.separator);
            //添加元素
            queue.offer(map);
        }
    }

    /**
     * 多线程批量导出 excel
     *
     * @param response 用于浏览器下载
     * @throws InterruptedException
     */
    public void threadCsv(HttpServletResponse response) throws InterruptedException {
        initQueue();
        long start = System.currentTimeMillis();
        //异步转同步，等待所有线程都执行完毕返会 主线程才会结束
        try {
            CountDownLatch cdl = new CountDownLatch(queue.size());
            while (queue.size() > 0) {
                asyncTaskService.executeAsyncTask(queue.poll(), cdl);
            }
            cdl.await();

            //压缩文件
            File zipFile = new File(filePath.substring(0, filePath.length() - 1) + ".zip");
            FileOutputStream fos1 = new FileOutputStream(zipFile);
            //压缩文件目录
            ZipUtils.toZip(filePath, fos1, true);
            //发送zip包
            //ZipUtils.sendZip(response, zipFile);
            ZipUtils.sendFileSystem(zipFile);
            System.out.println(zipFile.getName() + " 文件生成成功");
            System.out.println("zip文件的大小：" + zipFile.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println("任务执行完毕       共消耗   ：  " + (end - start) / 1000 + " 秒");
    }

}
