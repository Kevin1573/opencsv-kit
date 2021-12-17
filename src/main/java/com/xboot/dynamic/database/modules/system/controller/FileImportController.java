package com.xboot.dynamic.database.modules.system.controller;

import com.xboot.dynamic.database.modules.system.entity.ImportAsyncInfo;
import com.xboot.dynamic.database.modules.system.service.impl.FileImportServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("file_import/v1")
public class FileImportController /*extends AbstractAPIController*/ {

    @Resource
    private FileImportServiceImpl fileImportService;

    private ExecutorService executor = Executors.newCachedThreadPool();

    //下载导入模板
    @RequestMapping("/excelExport")
    public void excelExport(HttpServletResponse response) {
        // studentImportService.excelExport(response);
    }

    //数据导入处理
    @RequestMapping("/save_csv_auto")
    public Map<String, Object> saveFile(HttpServletResponse response, @RequestParam("file") MultipartFile file) {
        Map<String, Object> m = new HashMap<>();
        String uuid = ImportAsyncInfo.createAsyncInfo();
        try {
            final InputStream inputStream = file.getInputStream();
            executor.submit(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    try {

                        fileImportService.saveExcel(response, inputStream, uuid);
                        // studentImportService.saveExcel_auto_studentno(response, inputStream,uuid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ImportAsyncInfo.getAsyncInfo(uuid).setMsg(e.getMessage());
                        ImportAsyncInfo.getAsyncInfo(uuid).setEnd(true);
                        throw new Exception("无法进行导入!");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        m.put("uuid", uuid);
        return m;
    }

    //下载导入的错误文件
    @RequestMapping("downloadErrorExcel")
    public void downloadErrorExcel(HttpServletResponse response, String fileName) {
        // studentImportService.downloadErrorExcel(response, fileName);
    }

    //获取导入的进度
    @RequestMapping("get_import_plan")
    public Map<String, Object> get_import_plan(String uuid) {
        Map<String, Object> m = new HashMap<>();
        ImportAsyncInfo asyncInfo = ImportAsyncInfo.getAsyncInfo(uuid);
        //如果导入结束,复制进度对象进行返回,将储存的进度对象删除
        if (asyncInfo != null && asyncInfo.getEnd().get()) {
            ImportAsyncInfo newAsyncInfo = new ImportAsyncInfo();
            newAsyncInfo.setEnd(asyncInfo.getEnd().get());
            newAsyncInfo.setMsg(asyncInfo.getMsg());
            newAsyncInfo.setErrorFilePath(asyncInfo.getErrorFilePath());
            newAsyncInfo.setTotality(asyncInfo.getTotality().get());
            newAsyncInfo.setDoneSum(asyncInfo.getDoneSum().get());
            newAsyncInfo.setErrorSum(asyncInfo.getErrorSum().get());
            newAsyncInfo.setSuccessSum(asyncInfo.getSuccessSum().get());
            ImportAsyncInfo.deleteAsyncInfo(uuid);
            asyncInfo = newAsyncInfo;
        }
        m.put("data", asyncInfo);
        return m;
    }
}
