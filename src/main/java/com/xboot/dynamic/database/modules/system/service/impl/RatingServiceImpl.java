package com.xboot.dynamic.database.modules.system.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.xboot.dynamic.database.modules.system.entity.ImportAsyncInfo;
import com.xboot.dynamic.database.modules.system.entity.ImportDataRecord;
import com.xboot.dynamic.database.modules.system.entity.Ratings;
import com.xboot.dynamic.database.modules.system.mapper.ImportDataRecordMapper;
import com.xboot.dynamic.database.modules.system.mapper.RatingImportMapper;
import com.xboot.dynamic.database.modules.system.service.IRatingService;
import com.xboot.dynamic.database.modules.system.utils.CommonLineReaderUtil;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RatingServiceImpl extends ServiceImpl<RatingImportMapper, Ratings> implements IRatingService {
    private ExecutorService executor = Executors.newCachedThreadPool();
    @Resource
    RatingImportMapper ratingImportMapper;
    @Resource
    ImportDataRecordMapper importDataRecordMapper;

    @Override
    public Map<String, String> importAndExportData(InputStreamReader isr, String uuid, String zipFilePath) {
        CommonLineReaderUtil readerUtil = new CommonLineReaderUtil();
        String[] header = new String[]{"userId", "movieId", "rating", "timestamp", "value", "content", "date"};
        Map<String, String> zipFileInfo = new HashMap<>();
        String charset = "utf-8";
        Iterator<String[]> recordIter = null;
        RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().build();
        try (CSVReader readerCsv = new CSVReaderBuilder(isr)
                .withCSVParser(rfc4180Parser).build()) {
            recordIter = readerCsv.iterator();
            final QueryWrapper<Ratings> removeWrapper = new QueryWrapper<>();
            removeWrapper.eq("batch_no", uuid);
            remove(removeWrapper);

            // userId  movieId	rating	timestamp
            List<String[]> firstFiveRecord = new ArrayList<>();
            List<Ratings> dataRecords = new ArrayList<>();
            int line = 0;
            int batchSize = 0;

            {
                final String[] headers = recordIter.next();
                firstFiveRecord.add(headers);
                line++;
            }

            for (Iterator<String[]> it = recordIter; it.hasNext(); ) {
                String[] csvRecord = it.next();// 第一行不会被打印出来

//                if (line < 5) {
//                    // 模拟前5行数据
//                    firstFiveRecord.add(new ArrayList<>());
//                    System.out.println(csvRecord.isConsistent() + "  " + csvRecord.getRecordNumber() + "  "
//                            + (csvRecord.isMapped("movieId") ? csvRecord.get("movieId") : ""));
//                    line++;
//                    continue;
//                }
                // 校验当前行数据，如果有错误写入到ImportAsyncInfo对象中
                boolean isValid = readerUtil.validateData(csvRecord, uuid);
                if (isValid) {
                    dataRecords.add(convertRecord(++line, csvRecord, uuid));
                    batchSize++;
                }
                // 将数据批量写入到临时表中 -- 多线程写入
                if (batchSize % 120_000 == 0) {
                    System.out.println("正在写入数据," + dataRecords.size() + " 条");
                    saveBatch(dataRecords, dataRecords.size());
                    dataRecords = new ArrayList<>();
                }

            }

            // 不满足一批次的数据
            if (dataRecords.size() > 0) {
                System.out.println("最后一批-正在写入数据," + dataRecords.size() + " 条");
                saveBatch(dataRecords, dataRecords.size());
                System.out.println("最后一批写入完成!");
            }

            zipFileInfo = exportDataFromDbJoin("UTF-8", uuid, zipFilePath, header);
        } catch (IOException e) {
            System.out.println("读取csv文件出错");
            e.printStackTrace();
        }

        return zipFileInfo;
    }

    private Map<String, String> exportDataFromDbJoin(String charset, String uuid, String zipFilePath, String... header) {
        CommonLineReaderUtil readerUtil = new CommonLineReaderUtil();
        Map<String, String> zipFileInfo = new HashMap<>();
        final File filePath = readerUtil.createTempDirName(zipFilePath, uuid);
        // 从DB中join出数据,分批写入到临时文件中 -- 单线程
        // batchSize 分批次写入到文件中
        long totalCount = countDataRecord(uuid);
        int batchSize = 10000 * 20;
        int batchNum = getBatchNumber(Math.toIntExact(totalCount), batchSize);
        for (int batchIndex = 0; batchIndex < batchNum; batchIndex++) {
//            executor.submit(()->{
//
//            });
            List<Ratings> dbData = batchSelectData(uuid, Math.toIntExact(totalCount), batchIndex, batchSize);
            System.out.println("第" + batchIndex + "批次,查询出 " + dbData.size() + " 条记录");
            // 每个批次生成一个文件
            readerUtil.processDataTaskA(dbData, filePath, charset, uuid, header);
        }

        // 合并压缩文件
        readerUtil.doZipForFileSystem(zipFileInfo, filePath);
        final ImportAsyncInfo asyncInfo = ImportAsyncInfo.getAsyncInfo(uuid);
        final long beginTime = asyncInfo.getBeginTime();
        final String costTime = (System.currentTimeMillis() - beginTime) / 1000 + " s";
        System.out.printf("共消耗了： %s", costTime);
        asyncInfo.setCostTime(System.currentTimeMillis());
        final ImportDataRecord dataRecord = new ImportDataRecord();
        dataRecord.setCostTime(String.valueOf(System.currentTimeMillis()));
        final QueryWrapper<ImportDataRecord> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("batch_no", uuid);
        importDataRecordMapper.update(dataRecord, updateWrapper);
        return zipFileInfo;
    }

    private int getBatchNumber(int totalCount, int batchSize) {
        if (totalCount > batchSize) {
            return totalCount % batchSize == 0 ? (totalCount / batchSize) : (totalCount / batchSize) + 1;
        } else {
            return 1;
        }
    }

    private List<Ratings> batchSelectData(String uuid, int totalCount, int batchIndex, int batchSize) {

        if (totalCount > batchSize) {
            List<Ratings> records = new ArrayList<>();
            // batchSize的大小必须是pageSize的整数倍
            int pageSize = 20000;
            if (batchSize % pageSize != 0) {
                throw new RuntimeException("batchSize 的大小必须是 pageSize 的整数倍");
            }

            for (int i = 0; i < batchSize / pageSize; i++) {
                final List<Ratings> ratingsList =
                        ratingImportMapper.selectPageA(uuid, (i * pageSize) + (batchIndex * batchSize), pageSize);
                records.addAll(ratingsList);
            }
            return records;
        } else {
            final List<Ratings> ratingsList =
                    ratingImportMapper.selectPageA(uuid, 0, totalCount);
            return ratingsList;
        }
    }

    private long countDataRecord(String uuid) {
        QueryWrapper<Ratings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("batch_no", uuid);
        return count(queryWrapper);
    }

    // userId	movieId	rating	timestamp
    private Ratings convertRecord(int recordNumber, String[] csvRecord, String uuid) {
        final Ratings ratings = new Ratings();
        ratings.setRowId(String.valueOf(recordNumber));
        ratings.setW3Account("wwx1114216");
        ratings.setBatchNo(uuid);
        ratings.setUserId(csvRecord[0]);
        ratings.setMovieId(csvRecord[1]);
        ratings.setRating(csvRecord[2]);
        ratings.setTimestamp(csvRecord[3]);
        ratings.setCreateTime(DateTime.now().toSqlDate());
        return ratings;
    }

    private Ratings convertRecord(int recordNumber, CSVRecord csvRecord, String uuid) {
        final Ratings ratings = new Ratings();
        ratings.setRowId(Long.toString(csvRecord.getRecordNumber()));
        ratings.setW3Account("wwx1114216");
        ratings.setBatchNo(uuid);
        ratings.setUserId(csvRecord.get("userId"));
        ratings.setMovieId(csvRecord.get("movieId"));
        ratings.setRating(csvRecord.get("rating"));
        ratings.setTimestamp(csvRecord.get("timestamp"));
        ratings.setCreateTime(DateTime.now().toSqlDate());
        return ratings;
    }

    public int saveBatch(List<Ratings> entityList, int batchSize) {
        int result = 0;
        try {
            result = ratingImportMapper.batchInsertImportData(entityList);
        } finally {
            // entityList.clear();
        }
        return result;
    }

    @Override
    public long count(Wrapper<Ratings> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    public <E extends IPage<Ratings>> E page(E page, Wrapper<Ratings> queryWrapper) {
        return super.page(page, queryWrapper);
    }

    @Override
    public boolean remove(Wrapper<Ratings> queryWrapper) {
        return super.remove(queryWrapper);
    }
}
