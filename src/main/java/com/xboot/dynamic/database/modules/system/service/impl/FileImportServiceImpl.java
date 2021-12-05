package com.xboot.dynamic.database.modules.system.service.impl;

import com.xboot.dynamic.database.modules.system.entity.ImportAsyncInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Map;

@Service
public class FileImportServiceImpl {
//    @Override
    public void excelExport(HttpServletResponse response) {
        //导入模板下载   略
    }

//    @Override
    public void downloadErrorExcel(HttpServletResponse response, String fileName) {
        //下载错误文件   略
    }

    @Transactional
//    @Override
    public Map<String, Object> saveExcel(HttpServletResponse response, InputStream inputStream, String uuid) {
        //其他代码...


        //获取excel导入数据数量后
        Integer 数量 = null;
        ImportAsyncInfo.getAsyncInfo(uuid).setTotality( 数量 );
        //其他代码...
        for (int i = 0; i < 数量; i++) {
            //其他代码...
            //在一条数据处理结束后
            ImportAsyncInfo.doneSumAddOne(uuid);
            //其他代码...
            boolean 数据有错误 = false;
            if(数据有错误){
                //其他代码...
                ImportAsyncInfo.errorSumAddOne(uuid);
            }else{
                //其他代码...
                ImportAsyncInfo.successSumAddOne(uuid);
            }
        }
        //其他代码...
        //错误文件创建后
        String errorFileName = new String();
        ImportAsyncInfo.getAsyncInfo(uuid).setErrorFilePath(errorFileName);
        //其他代码...
        //导入完成后
        ImportAsyncInfo.getAsyncInfo(uuid).setEnd(true);

        return null;
    }
}
