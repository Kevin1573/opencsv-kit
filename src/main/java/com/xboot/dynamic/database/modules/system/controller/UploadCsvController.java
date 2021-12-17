package com.xboot.dynamic.database.modules.system.controller;

import com.xboot.dynamic.database.modules.system.entity.ImportAsyncInfo;
import com.xboot.dynamic.database.modules.system.utils.CommonLineReaderUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class UploadCsvController {

    private ExecutorService executor = Executors.newCachedThreadPool();

    @PostMapping("/uploadCache")
    public Object uploadFileCaching(
            HttpServletResponse response,
            @RequestParam("file") MultipartFile multipartFile) {
        Runtime rt = Runtime.getRuntime();
//        rt.gc();

        long startMem = rt.freeMemory(); // 开始时的剩余内存


        getRuntimeMem();
        int MB = 1024 * 1024;

        int countLineNum = 0;
        ByteArrayOutputStream baos = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            baos = transInputStream(multipartFile.getInputStream());
            inputStream = new ByteArrayInputStream(baos.toByteArray());
            countLineNum = countLineNum(inputStream);
            System.out.println(countLineNum);
            inputStream = new ByteArrayInputStream(baos.toByteArray());
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                // System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("开始时的剩余内存:" + mb(startMem));
        long orz = startMem - rt.freeMemory(); // 剩余内存 现在
        System.out.println("剩余内存:" + mb(orz));

        System.out.println("Free Mem: " + rt.freeMemory() / MB + "   total Memory: " + rt.totalMemory() / MB + "   Max Memory: " + rt.maxMemory() / MB);

        return countLineNum;
    }

    static String mb(long s) {
        return String.format("%d (%.2f M)", s, (double) s / (1024 * 1024));
    }

    static void getRuntimeMem() {
        System.out.println("Runtime max: " + mb(Runtime.getRuntime().maxMemory()));
        MemoryMXBean m = ManagementFactory.getMemoryMXBean();

        System.out.println("Non-heap: " + mb(m.getNonHeapMemoryUsage().getMax()));
        System.out.println("Heap: " + mb(m.getHeapMemoryUsage().getMax()));

        for (MemoryPoolMXBean mp : ManagementFactory.getMemoryPoolMXBeans()) {
            System.out.println("Pool: " + mp.getName() +
                    " (type " + mp.getType() + ")" +
                    " = " + mb(mp.getUsage().getMax()));
        }
    }


    @PostMapping("/upload")
    public Object upload(HttpServletResponse response, @RequestParam("file") MultipartFile multipartFile) {

        // 初始化导入进度信息
        Map<String, Object> m = new HashMap<>();
        String uuid = ImportAsyncInfo.createAsyncInfo();
        try {

            final InputStream is = multipartFile.getInputStream();
            CommonLineReaderUtil commonLineReaderUtil = new CommonLineReaderUtil();
            executor.submit(() -> {
                InputStream cloneInputStream = null;
                try (ByteArrayOutputStream baos = transInputStream(is)) {
                    cloneInputStream = new ByteArrayInputStream(baos.toByteArray());
                    int totalCount = countLineNum(cloneInputStream);
                    //获取csv导入数据数量后
                    ImportAsyncInfo asyncInfo = ImportAsyncInfo.getAsyncInfo(uuid);
                    asyncInfo.getTotality().addAndGet(totalCount);

                    cloneInputStream = new ByteArrayInputStream(baos.toByteArray());
                    final InputStreamReader isr = new InputStreamReader(cloneInputStream, Charset.forName("UTF-8"));
                    commonLineReaderUtil.readInApacheIOWithThreadPool(response, isr, uuid);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ImportAsyncInfo.getAsyncInfo(uuid).setMsg(ex.getMessage());
                    ImportAsyncInfo.getAsyncInfo(uuid).setEnd(true);
                    // throw new Exception("无法进行导入!");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        m.put("uuid", uuid);
        return m;
    }

    public static int countLineNum(InputStream is) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(is, Charset.forName("UTF-8"));
             LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader)) {
            lineNumberReader.skip(Long.MAX_VALUE);
            int lineNumber = lineNumberReader.getLineNumber();
            return lineNumber + 1;//实际上是读取换行符数量 , 所以需要+1
        } catch (IOException e) {
            return -1;
        }
    }

    private static ByteArrayOutputStream transInputStream(InputStream is) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            // 打开一个新的输入流
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //获取导入的进度
    @RequestMapping("get_import_plan")
    public Map<String, Object> get_import_plan(String uuid) {
        Map<String, Object> m = new HashMap<>();
        ImportAsyncInfo asyncInfo = ImportAsyncInfo.getAsyncInfo(uuid);
        //如果导入结束,复制进度对象进行返回,将储存的进度对象删除
//        if (asyncInfo != null && asyncInfo.getEnd()) {
//            ImportAsyncInfo newAsyncInfo = new ImportAsyncInfo();
//            newAsyncInfo.setEnd(asyncInfo.getEnd());
//            newAsyncInfo.setMsg(asyncInfo.getMsg());
//            newAsyncInfo.setErrorFilePath(asyncInfo.getErrorFilePath());
//            newAsyncInfo.setTotality(asyncInfo.getTotality());
//            newAsyncInfo.setDoneSum(asyncInfo.getDoneSum());
//            newAsyncInfo.setErrorSum(asyncInfo.getErrorSum());
//            newAsyncInfo.setSuccessSum(asyncInfo.getSuccessSum());
//            // ImportAsyncInfo.deleteAsyncInfo(uuid);
//            asyncInfo = newAsyncInfo;
//        }
        m.put("data", asyncInfo);
        return m;
    }
}
