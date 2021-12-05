package com.xboot.dynamic.database.modules.system.utils;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.time.StopWatch;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class CommonLineReaderUtil {

    public void read() {
        StopWatch stopwatch = StopWatch.createStarted();
        try {
            Reader reader = new FileReader("D:\\temp\\ml-25m\\genome-scores-skip-5-header.csv");
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("movieId", "tagId", "relevance").parse(reader);// 定义后必须和csv文件中的标头一致
            List<String> extFieldData = new ArrayList<>();
            int line = 0;
            for (CSVRecord csvRecord : records) {// 第一行不会被打印出来
                if (line < 5) {
                    extFieldData.add(csvRecord.get("movieId"));
                    System.out.println(csvRecord.isConsistent() + "  " + csvRecord.getRecordNumber() + "  " + (csvRecord.isMapped("movieId") ? csvRecord.get("movieId") : ""));
                    // System.out.println(csvRecord.get("tagId") + "---" + csvRecord.get("relevance"));
                    line++;
                    continue;
                }

//                if (line > 50) {
//                    break;
//                }
                // System.out.println(csvRecord.get(0) + "---" + csvRecord.get(1) + "---" + csvRecord.get(2) );
                System.out.println(csvRecord.get("movieId") + "---" + csvRecord.get("tagId") + "---" + csvRecord.get("relevance"));
                line++;
            }
            stopwatch.stop();
            System.out.println(Arrays.toString(extFieldData.toArray()));
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("read all lines spend " + stopwatch.getTime(TimeUnit.MICROSECONDS) + " ms");


        // 计算内存占用
        logMemory();
    }

    public void logMemory() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        //堆内存使用情况
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        //初始的总内存
        long totalMemorySize = memoryUsage.getInit();
        //已使用的内存
        long usedMemorySize = memoryUsage.getUsed();

        System.out.println("Total Memory: " + totalMemorySize / (1024 * 1024) + " Mb");
        System.out.println("Free Memory: " + usedMemorySize / (1024 * 1024) + " Mb");
    }

    public void readInApacheIOWithThreadPool() {
        // 创建一个 最大线程数为 10，队列最大数为 100 的线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60l, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100));
        Reader reader = null;
        StopWatch stopwatch = StopWatch.createStarted();
        try {
            reader = new FileReader("D:\\temp\\ml-25m\\genome-scores-skip-5-header.csv");
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("movieId", "tagId", "relevance").parse(reader);// 定义后必须和csv文件中的标头一致
            List<String> extFieldData = new ArrayList<>();
            List<String[]> dataList = new ArrayList<>();
            File filePath = new File("D:/temp/Desktop");
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            int line = 0;
            for (CSVRecord csvRecord : records) {// 第一行不会被打印出来
                if (line < 5) {
                    extFieldData.add(csvRecord.get("movieId"));
                    System.out.println(csvRecord.isConsistent() + "  " + csvRecord.getRecordNumber() + "  " + (csvRecord.isMapped("movieId") ? csvRecord.get("movieId") : ""));
                    // System.out.println(csvRecord.get("tagId") + "---" + csvRecord.get("relevance"));
                    line++;
                    continue;
                }

//                if (line > 50) {
//                    break;
//                }

                dataList.add(new String[]{csvRecord.get("movieId"), csvRecord.get("tagId"), csvRecord.get("relevance")});

                if (dataList.size() == 100000) {
                    List<List<String[]>> partition = Lists.partition(dataList, 50000);
                    List<Future> futureList = new ArrayList<>();
                    for (List<String[]> strings : partition) {
                        Future<?> future = threadPoolExecutor.submit(() -> {
                            processTask(strings, filePath);
                        });
                        futureList.add(future);
                    }
                    // 等待两个线程将任务执行结束之后，再次读取数据。这样的目的防止，任务过多，加载的数据过多，导致 OOM
                    for (Future future : futureList) {
                        // 等待执行结束
                        future.get();
                    }
                    // 清除内容
                    dataList.clear();
                }
                // System.out.println(csvRecord.get(0) + "---" + csvRecord.get(1) + "---" + csvRecord.get(2) );
//                System.out.println(csvRecord.get("movieId") + "---" + csvRecord.get("tagId") + "---" + csvRecord.get("relevance"));
                line++;
            }

            // lines 若还有剩余，继续执行结束
            if (!dataList.isEmpty()) {
                // 继续执行
                processTask(dataList, filePath);
            }

            System.out.println(Arrays.toString(extFieldData.toArray()));


            stopwatch.stop();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            threadPoolExecutor.shutdown();

            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("read all lines spend " + stopwatch.getTime(TimeUnit.MICROSECONDS) + " ms");

        // 计算内存占用
        logMemory();
    }

    private void doZipForResponse(HttpServletResponse response, String filePath) {
        System.out.println("export zip file path is " + filePath);
        try {
            //压缩文件
            File zipFile = new File(filePath.substring(0, filePath.length() - 1) + ".zip");
            FileOutputStream fos1 = new FileOutputStream(zipFile);
            //压缩文件目录
            ZipUtils.toZip(filePath, fos1, true);
            //发送zip包
            ZipUtils.sendZip(response, zipFile);
            // ZipUtils.sendFileSystem(zipFile);
            System.out.println(zipFile.getName() + " 文件生成成功");
            System.out.println("zip文件的大小：" + zipFile.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void readInApacheIOWithThreadPool(HttpServletResponse response, Reader reader) {
        // 创建一个 最大线程数为 10，队列最大数为 100 的线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60l, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100));
        StopWatch stopwatch = StopWatch.createStarted();
        try {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("movieId", "tagId", "relevance").parse(reader);// 定义后必须和csv文件中的标头一致
            List<String> extFieldData = new ArrayList<>();
            List<String[]> dataList = new ArrayList<>();
            File filePath = new File("D:/temp/Desktop");
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            int line = 0;
            for (CSVRecord csvRecord : records) {// 第一行不会被打印出来
                if (line < 5) {
                    extFieldData.add(csvRecord.get("movieId"));
                    System.out.println(csvRecord.isConsistent() + "  " + csvRecord.getRecordNumber() + "  " + (csvRecord.isMapped("movieId") ? csvRecord.get("movieId") : ""));
                    // System.out.println(csvRecord.get("tagId") + "---" + csvRecord.get("relevance"));
                    line++;
                    continue;
                }

//                if (line > 50) {
//                    break;
//                }

                dataList.add(new String[]{csvRecord.get("movieId"), csvRecord.get("tagId"), csvRecord.get("relevance")});

                if (dataList.size() == 100000) {
                    List<List<String[]>> partition = Lists.partition(dataList, 50000);
                    List<Future> futureList = new ArrayList<>();
                    for (List<String[]> strings : partition) {
                        Future<?> future = threadPoolExecutor.submit(() -> {
                            processTask(strings, filePath);
                        });
                        futureList.add(future);
                    }
                    // 等待两个线程将任务执行结束之后，再次读取数据。这样的目的防止，任务过多，加载的数据过多，导致 OOM
                    for (Future future : futureList) {
                        // 等待执行结束
                        future.get();
                    }
                    // 清除内容
                    dataList.clear();
                }
                // System.out.println(csvRecord.get(0) + "---" + csvRecord.get(1) + "---" + csvRecord.get(2) );
//                System.out.println(csvRecord.get("movieId") + "---" + csvRecord.get("tagId") + "---" + csvRecord.get("relevance"));
                line++;
            }

            // lines 若还有剩余，继续执行结束
            if (!dataList.isEmpty()) {
                // 继续执行
                processTask(dataList, filePath);
            }

            System.out.println(Arrays.toString(extFieldData.toArray()));


            doZipForResponse(response , filePath.getPath());

            stopwatch.stop();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            threadPoolExecutor.shutdown();

            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("read all lines spend " + stopwatch.getTime(TimeUnit.MICROSECONDS) + " ms");

        // 计算内存占用
        logMemory();
    }



    private static void processTask(List<String[]> strings, File file) {
        long timeMillis = System.currentTimeMillis();

        try {
            Appendable printWriter = new PrintWriter(file + "/CSV-TEST" + timeMillis + ".csv", "GBK");//指定GBK,解决Microsoft不兼容
            CSVPrinter csvPrinter = CSVFormat.EXCEL.withHeader("姓名", "性别", "年龄", "生日").print(printWriter);
            int row = 0;
            for (String[] line : strings) {
                // 模拟业务执行
                // System.out.println(Thread.currentThread().getName() + ": " + Arrays.toString(line));
                csvPrinter.printRecord(line[0], "M" + line[1], line[2], new Date());
                row++;
                if(row % 10000 == 0){
                    csvPrinter.flush();
                }
            }
            csvPrinter.flush();
            csvPrinter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CommonLineReaderUtil commonLineReaderUtil = new CommonLineReaderUtil();
        commonLineReaderUtil.readInApacheIOWithThreadPool();
//        commonLineReaderUtil.read();
    }
}
