package com.xboot.dynamic.database.modules.system.utils;

import info.monitorenter.cpdetector.io.*;

import java.io.*;
import java.net.URL;

public class FileUtil {
    /**
     * 利用第三方开源包cpdetector获取文件编码格式
     *
     * @param path 要判断文件编码格式的源文件的路径
     */
    public static String getFileEncode(String path) {
        /*
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
         * JChardetFacade、ASCIIDetector、UnicodeDetector。
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
         * cpDetector是基于统计学原理的，不保证完全正确。
         */
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        /*
         * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
         * 指示是否显示探测过程的详细信息，为false不显示。
         */
        detector.add(new ParsingDetector(false));
        /*
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
         * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
         * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
         */
        detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
        // ASCIIDetector用于ASCII编码测定
        detector.add(ASCIIDetector.getInstance());
        // UnicodeDetector用于Unicode家族编码的测定
        detector.add(UnicodeDetector.getInstance());
        java.nio.charset.Charset charset = null;
        File f = new File(path);
        try {
            charset = detector.detectCodepage(f.toURI().toURL());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (charset != null)
            return charset.name();
        else
            return null;
    }


    /**
     * 利用第三方开源包cpdetector获取URL对应的文件编码
     *
     * @param url 要判断文件编码格式的源文件的URL
     */
    public static String getFileEncode(URL url) {
        /*
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
         * JChardetFacade、ASCIIDetector、UnicodeDetector。
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
         * cpDetector是基于统计学原理的，不保证完全正确。
         */
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        /*
         * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
         * 指示是否显示探测过程的详细信息，为false不显示。
         */
        detector.add(new ParsingDetector(false));
        /*
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
         * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
         * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
         */
        detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
        // ASCIIDetector用于ASCII编码测定
        detector.add(ASCIIDetector.getInstance());
        // UnicodeDetector用于Unicode家族编码的测定
        detector.add(UnicodeDetector.getInstance());
        java.nio.charset.Charset charset = null;
        try {
            charset = detector.detectCodepage(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (charset != null)
            return charset.name();
        else
            return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
        String configFilePath = "d:/temp/test5.csv";
        String charsetName = getFileEncode(configFilePath);
        System.out.println(charsetName);
        //x-EUC-TW
        String configFile = configFilePath;
        InputStream inputStream = new FileInputStream(configFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, charsetName));
        //读jar包内部资源文件(先利用cpdetector检测jar内部的资源文件的编码格式，然后以检测到的编码方式去读文件)：

        // ===========================================
//        URL url = CreateStationTreeModel.class.getResource("/resource/" + "配置文件");
//        URLConnection urlConnection = url.openConnection();
//        inputStream=urlConnection.getInputStream();
//        String charsetName = getFileEncode(url);
//        System.out.println(charsetName);
//        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, charsetName));
    }

    /**
     * 获取二进制文件字节流中内容的编码格式
     * utf-8:对应二进制编码格式头为-17 -69 -65 十进制为：EF BB BF
     * unicode:对应二进制编码格式头为-1 -2  十进制为：FF FE
     * gbk格式没有自己编码头，所以无法比较.且gbk2312与gbk是包含关系
     */
    public static String getCodeType(byte[] b) {
        if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
            return "utf8";
        } else if (b[0] == -1 && b[1] == -2) {
            return "unicode";
        } else {
            return "gbk";
        }
    }
}
