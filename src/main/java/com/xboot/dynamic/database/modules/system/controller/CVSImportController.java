package com.xboot.dynamic.database.modules.system.controller;

import com.opencsv.*;
import com.xboot.dynamic.database.modules.system.entity.TData;
import com.xboot.dynamic.database.modules.system.service.ICSVService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/file/csv")
public class CVSImportController {

    private ICSVService icsvService;

    public CVSImportController(ICSVService icsvService) {
        this.icsvService = icsvService;
    }

    @GetMapping("/download")
    public String downLoad(HttpServletResponse response) throws UnsupportedEncodingException {
        String filename = "2.csv";
        String filePath = "D:/temp";
        File file = new File(filePath + "/" + filename);
        if (file.exists()) { //判断文件父目录是否存在
            System.out.println("downloading...");
            // response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            downloadFile(response, file);
        }
        return null;
    }

    /**
     * @return boolean
     * @Description 下载文件
     * @Param response，file
     **/
    public static boolean downloadFile(HttpServletResponse response, File file) {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        OutputStream os = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            os = response.getOutputStream();
            //MS产本头部需要插入BOM
            //如果不写入这几个字节，会导致用Excel打开时，中文显示乱码
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            byte[] buffer = new byte[1024];
            int i = bufferedInputStream.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bufferedInputStream.read(buffer);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            file.delete();
        }
        return false;
    }

    @PostMapping("/")
    @ResponseBody
    public Map<String, Object> importFile(@RequestParam("file") MultipartFile file) {
        List<Integer> ids = new LinkedList<>();
        List<String[]> csvDataList = new LinkedList<>();
        String charset = "utf-8";
        RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().build();
        try (CSVReader readerCsv = new CSVReaderBuilder(new InputStreamReader(file.getInputStream(), charset))
                .withCSVParser(rfc4180Parser).build()) {
            String[] lines;
            while ((lines = readerCsv.readNext()) != null) {
                System.out.println(Arrays.toString(lines));
                System.out.println("---------------");
                ids.add(Integer.valueOf(lines[0]));
                csvDataList.add(lines);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Integer, TData> tDataMap = icsvService.getAlls(ids)
                .stream().collect(Collectors.toMap(TData::getId, tData -> tData));

        exportNewCsv(csvDataList, tDataMap);

        HashMap<String, Object> result = new HashMap<>();
        result.put("status", 200);
        result.put("data", "ok");
        return result;
    }

    private void exportNewCsv(List<String[]> csvDataList, Map<Integer, TData> tDataMap) {
        OutputStreamWriter is = null;
        ICSVWriter build;
        try {
            List<String[]> transFormData = prepareProcessData(csvDataList, tDataMap);
            is = new OutputStreamWriter(new FileOutputStream("D:\\temp\\2.csv"), "utf-8");
            build = new CSVWriterBuilder(is).build();
            build.writeAll(transFormData, false);
            build.flushQuietly();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private List<String[]> prepareProcessData(List<String[]> csvDataList, Map<Integer, TData> tDataMap) {
        List<String[]> collect = csvDataList.stream().map(csvData -> {
            String id = csvData[0];
            TData tData = tDataMap.get(Integer.valueOf(id));
            String[] destData = new String[csvData.length + 1];
            System.arraycopy(csvData, 0, destData, 0, csvData.length);
            if (tData != null) {
                if (!StringUtils.isEmpty(tData.getName())) {
                    destData[destData.length - 1] = tData.getName();
                } else {
                    destData[destData.length - 1] = new String("null");
                }
            }
            return destData;
        }).collect(Collectors.toList());
        return collect;
    }
}
