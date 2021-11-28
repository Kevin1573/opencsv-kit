package com.xboot.dynamic.database;

public class PathGen {

    public static final String separator = System.getProperty("file.separator");

    private PathGen() {}

    public static String paths(String moduleName) {

        String userDir = System.getProperty("user.dir");
        String fileSeparator = separator;
        String projectDir  = userDir + fileSeparator + moduleName +fileSeparator+"src";
        return projectDir;
    }

    public static void main(String[] args) {
        String projectDir  = paths("");
        System.out.println(projectDir);
    }
}
