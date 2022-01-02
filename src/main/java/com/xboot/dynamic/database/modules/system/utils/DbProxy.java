package com.xboot.dynamic.database.modules.system.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DbProxy {
    private static List<Map<String, String>> csvRecords = new ArrayList<>();

    private DbProxy() {
    }

    public static void insertData2Db(Map<String, String> csvRecord) {
        csvRecords.add(csvRecord);
    }


    public static void main(String[] args) {
        final String s = UUID.randomUUID().toString();
        System.out.println(s);
    }
}
