package com.xboot.dynamic.database;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.xboot.dynamic.database.modules.system.utils.FileUtil;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class CsvEncodeTest {
    public static void main(String[] args) throws IOException {
//        windows-1252
        String filepath = "d:/temp/test1.csv";
        String charset = FileUtil.getFileEncode(filepath);
        System.out.println(charset);
        File file = new File(filepath);
        InputStream fis = new FileInputStream(file);
//        byte[] bytes = fis.readNBytes(3);
//        String codeType = FileUtil.getCodeType(bytes);
//        System.out.println(codeType);
//        charset = codeType;
//        fis = new FileInputStream(file);
        RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().build();
        System.out.println(">>>" + charset);
        try (CSVReader readerCsv = new CSVReaderBuilder(new InputStreamReader(fis, charset))
                .withCSVParser(rfc4180Parser).build()) {
            String[] lines;
            while ((lines = readerCsv.readNext()) != null) {
                System.out.println(Arrays.toString(lines));
                System.out.println("---------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String getRealEncode(String encodeType) {
        List<String> asList = Arrays.asList("x-EUC-TW", "");
        return asList.contains(encodeType) ? "gbk" : encodeType;
    }
}
