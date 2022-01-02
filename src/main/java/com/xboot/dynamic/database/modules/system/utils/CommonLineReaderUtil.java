package com.xboot.dynamic.database.modules.system.utils;

import cn.hutool.core.lang.generator.UUIDGenerator;
import com.google.common.collect.Lists;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.xboot.dynamic.database.modules.system.entity.ImportAsyncInfo;
import com.xboot.dynamic.database.modules.system.entity.Ratings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;

public class CommonLineReaderUtil {
    // 创建一个 最大线程数为 10，队列最大数为 100 的线程池
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60l, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100));


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
            final String uuid = "";
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
                            processTask(strings, filePath, uuid);
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
                processTask(dataList, filePath, uuid);
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

    public void doZipForFileSystem(Map zipFileInfo, File filePath) {
        System.out.println("export zip file path is " + filePath);
        try {
            final String path = filePath.getPath();
            //压缩文件
            File zipFile = new File(path + ".zip");
            FileOutputStream fos = new FileOutputStream(zipFile);
            //压缩文件目录
            ZipUtils.toZip(path, fos, true);
            //发送zip包
            ZipUtils.sendFileSystem(zipFile);
            System.out.println(zipFile.getName() + " 文件生成成功");
            System.out.println("zip文件的大小：" + zipFile.length());
            zipFileInfo.put("absolutePath", zipFile.getAbsoluteFile());
            zipFileInfo.put("path", zipFile.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 删除临时文件加
//            try {
//                Files.delete(Paths.get(filePath.getPath()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            // deleteTempDir(filePath.getPath());
        }

    }

    private void doZipForResponse(HttpServletResponse response, File filePath) {
        System.out.println("export zip file path is " + filePath);
        try {
            final String path = filePath.getPath();
            //压缩文件
            File zipFile = new File(path.substring(0, path.length() - 1) + ".zip");
            FileOutputStream fos1 = new FileOutputStream(zipFile);
            //压缩文件目录
            ZipUtils.toZip(path, fos1, true);
            //发送zip包
            // ZipUtils.sendZip(response, zipFile);
            ZipUtils.sendFileSystem(zipFile);
            System.out.println(zipFile.getName() + " 文件生成成功");
            System.out.println("zip文件的大小：" + zipFile.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 删除临时文件加
            try {
                Files.delete(Paths.get(filePath.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // deleteTempDir(filePath.getPath());
        }

    }

    private void deleteTempDir(String tempPath) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Path path = Paths.get(tempPath);
        try {
            Files.walkFileTree(path,
                    new SimpleFileVisitor<Path>() {
                        // 先去遍历删除文件
                        @Override
                        public FileVisitResult visitFile(Path file,
                                                         BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            System.out.printf("文件被删除 : %s%n", file);
                            return FileVisitResult.CONTINUE;
                        }

                        // 再去遍历删除目录
                        @Override
                        public FileVisitResult postVisitDirectory(Path dir,
                                                                  IOException exc) throws IOException {
                            Files.delete(dir);
                            System.out.printf("文件夹被删除: %s%n", dir);
                            return FileVisitResult.CONTINUE;
                        }

                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readInApacheIOWithThreadPool(HttpServletResponse response, Reader reader) {
        // 创建一个 最大线程数为 10，队列最大数为 100 的线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60l, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100));
        StopWatch stopwatch = StopWatch.createStarted();
        try {
            final String uuid = "";
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
                            processTask(strings, filePath, uuid);
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
                processTask(dataList, filePath, uuid);
            }

            System.out.println(Arrays.toString(extFieldData.toArray()));


            doZipForResponse(response, filePath);

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

    public void readInApacheIOWithThreadPoolA(HttpServletResponse response, Reader reader, String uuid) {

        try {

            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("movieId", "tagId", "relevance").parse(reader);// 定义后必须和csv文件中的标头一致
            List<List<String>> firstFiveRecord = new ArrayList<>();
            int line = 0;
            for (CSVRecord csvRecord : records) {// 第一行不会被打印出来
                if (line < 5) {
                    // 模拟前5行数据
                    firstFiveRecord.add(new ArrayList<>());
                    System.out.println(csvRecord.isConsistent() + "  " + csvRecord.getRecordNumber() + "  " + (csvRecord.isMapped("movieId") ? csvRecord.get("movieId") : ""));
                    line++;
                    continue;
                }
                // 校验当前行数据，如果有错误写入到ImportAsyncInfo对象中
                validateData(csvRecord, uuid);

                // 将数据批量写入到临时表中 -- 多线程写入
                // TODO insertData2DB
                DbProxy.insertData2Db(csvRecord.toMap());
                line++;
            }

            String[] header = new String[]{"value", "content", "date"};
            exportDataFromDbJoin(response, "UTF-8", uuid, header);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public File createTempDirName(String zipFilePath, String uuid) {
        // 临时文件目录
        String randomDir = uuid;
        File filePath = new File(zipFilePath + "/" + randomDir);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        return filePath;
    }

    public Map exportDataFromDbJoin(String charset, String uuid, String... header) {
        Map zipFileInfo = new HashMap();
        final File filePath = createTempDirName("D:/temp/Desktop", uuid);
        // 从DB中join出数据,分批写入到临时文件中 -- 单线程
        // batchSize 分批次写入到文件中
        int totalCount = selectDataCount();
        int pageSize = 1000;
        int pageIndex = 1;
        int batchSize = 10000;
        int pageNum = totalCount % pageSize == 0 ? totalCount / pageSize : (int) (totalCount / pageSize) + 1;
        for (int i = 0; i < pageNum; i++) {
            List<Map<String, String>> dbData = selectData(pageSize, pageIndex++, batchSize);
            // 每个批次生成一个文件
            processDataTask(dbData, filePath, charset, uuid, header);
        }

        // 合并压缩文件
        doZipForFileSystem(zipFileInfo, filePath);
        return zipFileInfo;
    }

    private void exportDataFromDbJoin(HttpServletResponse response, String charset, String uuid, String... header) {

        final File filePath = createTempDirName("d:/temp/Desktop", uuid);
        // 从DB中join出数据,分批写入到临时文件中 -- 单线程
        // batchSize 分批次写入到文件中
        int totalCount = selectDataCount();
        int pageSize = 1000;
        int pageIndex = 1;
        int batchSize = 10000;
        int pageNum = totalCount % pageSize == 0 ? totalCount / pageSize : (int) (totalCount / pageSize) + 1;
        for (int i = 0; i < pageNum; i++) {
            List<Map<String, String>> dbData = selectData(pageSize, pageIndex++, batchSize);
            // 每个批次生成一个文件
            processDataTask(dbData, filePath, charset, uuid, header);
        }

        // 合并压缩文件
        doZipForResponse(response, filePath);
    }

    public void processDataTaskA(List<Ratings> data, File file, String charset, String uuid, String... header) {
        long timeMillis = System.currentTimeMillis();
        ICSVWriter icsvWriter = null;
        try {
            final File tempFile = new File(file + "/export-" + timeMillis + ".csv");
            final OutputStream out = new FileOutputStream(tempFile);
            // 写入bom, 防止中文乱码
            byte[] bytes = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            out.write(bytes);
            OutputStreamWriter writer = new OutputStreamWriter(out, charset);

            icsvWriter = new CSVWriterBuilder(writer).build();
            icsvWriter.writeNext(header);
            for (Ratings line : data) {
                icsvWriter.writeNext(new String[]{line.getUserId(),
                        line.getMovieId(), line.getRating(), line.getTimestamp()});

                //在一条数据处理结束后
                ImportAsyncInfo.doneSumAddOne(uuid);
            }

            icsvWriter.writeNext(new String[]{new UUIDGenerator().next()});
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert icsvWriter != null;
            icsvWriter.flushQuietly();
        }
    }

    public void processDataTask(List<Map<String, String>> data, File file, String charset, String uuid, String... header) {
        long timeMillis = System.currentTimeMillis();
        ICSVWriter icsvWriter = null;
        try {
            final File tempFile = new File(file + "/export-" + timeMillis + ".csv");
            final OutputStream out = new FileOutputStream(tempFile);
            // 写入bom, 防止中文乱码
            byte[] bytes = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            out.write(bytes);
            OutputStreamWriter writer = new OutputStreamWriter(out, charset);

            icsvWriter = new CSVWriterBuilder(writer).build();
            icsvWriter.writeNext(header);
            for (Map<String, String> line : data) {
                // 模拟业务执行

                final String dateStr = "2021-12-0" + (RandomUtils.nextInt() / 30);
                icsvWriter.writeNext(new String[]{line.getOrDefault("key", "null"),
                        line.get("content"), dateStr.substring(0, 10)});

                //在一条数据处理结束后
                ImportAsyncInfo.doneSumAddOne(uuid);
            }

            icsvWriter.writeNext(new String[]{new UUIDGenerator().next().toString()});
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert icsvWriter != null;
            icsvWriter.flushQuietly();
        }
    }

    private int selectDataCount() {
        return 12000;
    }

    private List<Map<String, String>> selectData(int pageSize, int pageIndex, int batchSize) {
        List<Map<String, String>> dataResult = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            final HashMap<String, String> map = new HashMap<>();
            dataResult.add(map);

            map.put("key", "value");
            map.put("content", "(\"\\t\"+ 数据 +\"\\t\")");
        }


        return dataResult;
    }


    public void readInApacheIOWithThreadPool(HttpServletResponse response, Reader reader, String uuid) {
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

                // 检查当前行数据
                validateData(csvRecord, uuid);

                dataList.add(new String[]{csvRecord.get("movieId"), csvRecord.get("tagId"), csvRecord.get("relevance")});

                if (dataList.size() == 100000) {
//                    //获取csv导入数据数量后
//                    ImportAsyncInfo asyncInfo = ImportAsyncInfo.getAsyncInfo(uuid);
//                    asyncInfo.getTotality().addAndGet(100000);
                    handleData(threadPoolExecutor, dataList, filePath, uuid);
                }
                // System.out.println(csvRecord.get(0) + "---" + csvRecord.get(1) + "---" + csvRecord.get(2) );
//                System.out.println(csvRecord.get("movieId") + "---" + csvRecord.get("tagId") + "---" + csvRecord.get("relevance"));
                line++;
            }

            // lines 若还有剩余，继续执行结束
            if (!dataList.isEmpty()) {
                //获取csv导入数据数量后
//                ImportAsyncInfo asyncInfo = ImportAsyncInfo.getAsyncInfo(uuid);
//                //asyncInfo.setTotality(asyncInfo.getTotality() + dataList.size());
//                asyncInfo.setTotality(dataList.size());
                // 继续执行
                processTask(dataList, filePath, uuid);
            }

            //其他代码...
            //错误文件创建后
            String errorFileName = new String();
            ImportAsyncInfo.getAsyncInfo(uuid).setErrorFilePath(errorFileName);
            //其他代码...

            // 打印扩展行的数据（前5行）
            System.out.println(Arrays.toString(extFieldData.toArray()));

            doZipForResponse(response, filePath);
            //导入完成后
            ImportAsyncInfo.getAsyncInfo(uuid).setEnd(true);
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

    /**
     * 复制输入流
     *
     * @param inputStream 请求输入流
     * @return 复制出来的输入流
     */
    public InputStream cloneInputStream(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public boolean validateData(CSVRecord csvRecord, String uuid) {
        //其他代码...
        boolean isError = false;
        if (isError) {
            //其他代码...
            ImportAsyncInfo.errorSumAddOne(uuid);
        } else {
            //其他代码...
            ImportAsyncInfo.successSumAddOne(uuid);
        }
        return true;
    }

    public boolean validateData(String[] csvRecord, String uuid) {
        //其他代码...
        boolean isError = false;
        if (isError) {
            //其他代码...
            ImportAsyncInfo.errorSumAddOne(uuid);
        } else {
            //其他代码...
            ImportAsyncInfo.successSumAddOne(uuid);
        }
        return true;
    }

    private void handleData(ThreadPoolExecutor threadPoolExecutor, List<String[]> dataList, File filePath, final String uuid) throws InterruptedException, ExecutionException {
        List<List<String[]>> partition = Lists.partition(dataList, 50000);
        List<Future> futureList = new ArrayList<>();
        for (List<String[]> strings : partition) {
            Future<?> future = threadPoolExecutor.submit(() -> {
                processTask(strings, filePath, uuid);
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


    private static void processTask(List<String[]> strings, File file, final String uuid) {
        long timeMillis = System.currentTimeMillis();
        CSVPrinter csvPrinter = null;
        try {
            Appendable printWriter = new PrintWriter(file + "/CSV-TEST" + timeMillis + ".csv", "GBK");//指定GBK,解决Microsoft不兼容
            csvPrinter = CSVFormat.EXCEL.withHeader("姓名", "性别", "年龄", "生日").print(printWriter);
            int row = 0;
            for (String[] line : strings) {
                // 模拟业务执行

                // System.out.println(Thread.currentThread().getName() + ": " + Arrays.toString(line));
                csvPrinter.printRecord(line[0], "M" + line[1], line[2], new Date());

                //在一条数据处理结束后
                ImportAsyncInfo.doneSumAddOne(uuid);
                row++;
                if (row % 10000 == 0) {
                    csvPrinter.flush();
                }
            }
            csvPrinter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (csvPrinter != null) {
                    csvPrinter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        CommonLineReaderUtil commonLineReaderUtil = new CommonLineReaderUtil();
        commonLineReaderUtil.readInApacheIOWithThreadPool();
//        commonLineReaderUtil.read();
    }
}
