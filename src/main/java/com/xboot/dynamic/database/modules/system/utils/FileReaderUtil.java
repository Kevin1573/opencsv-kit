package com.xboot.dynamic.database.modules.system.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FileReaderUtil {

    public static void read(String filePath) {
        if (Files.notExists(Paths.get(filePath))) {
            throw new RuntimeException("[ERROR] file is not exists");
        }
        //读取文件
        try {
            Stream<String> stringStream = Files
                    .lines(Paths.get(filePath), Charset.defaultCharset())
                    .flatMap(line -> Arrays.stream(line.split(" ")));
            stringStream.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> readCsv(String filePath) {
        List<String[]> csvDataList = new ArrayList<>();
        String charset = "utf-8";
        RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().build();

        try (InputStream inputStream = new FileInputStream(filePath);
             CSVReader readerCsv = new CSVReaderBuilder(new InputStreamReader(inputStream, charset))
                     .withCSVParser(rfc4180Parser).build()) {
            readerCsv.skip(500*10000);
            String[] lines;
            while ((lines = readerCsv.readNext()) != null) {

//                System.out.println(Arrays.toString(lines));
//                System.out.println("---------------");

                csvDataList.add(lines);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvDataList;
    }

    public static void main(String[] args) {
        String fileName = "D:/temp/mate_sys_log_202112032353.csv";
        readCsv(fileName);
    }
}
