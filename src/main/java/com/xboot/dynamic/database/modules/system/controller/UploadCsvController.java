package com.xboot.dynamic.database.modules.system.controller;

import com.xboot.dynamic.database.modules.system.utils.CommonLineReaderUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;

@RestController
public class UploadCsvController {

    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile multipartFile, HttpServletResponse response) {
        CommonLineReaderUtil commonLineReaderUtil = new CommonLineReaderUtil();
        InputStream is = null;
        InputStreamReader isr = null;
        try {
            is = multipartFile.getInputStream();
            isr = new InputStreamReader(is, Charset.forName("UTF-8"));
            commonLineReaderUtil.readInApacheIOWithThreadPool(response, isr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
