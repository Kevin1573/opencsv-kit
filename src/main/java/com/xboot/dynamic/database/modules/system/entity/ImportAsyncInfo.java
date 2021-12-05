package com.xboot.dynamic.database.modules.system.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用于存储csv导入的进度信息
 */
public class ImportAsyncInfo {
    //用于存储所有的导入进度信息
    public static Map<String, ImportAsyncInfo> allAsyncInfo = new HashMap<String, ImportAsyncInfo>();

    //提示信息或 异常信息
    private String msg;
    //数据总数
    private Integer totality = 0;
    //已处理的数据条数
    private Integer doneSum = 0;
    //失败的数据条数
    private Integer errorSum = 0;
    //成功的数据条数
    private Integer successSum = 0;
    //错误文件的路径
    public String errorFilePath;
    //导入是否结束
    public Boolean isEnd = false;

    /**
     * 创建一个进度信息,并获取对应的uuid
     *
     * @return
     */
    public static String createAsyncInfo() {
        ImportAsyncInfo asyncInfo = new ImportAsyncInfo();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        allAsyncInfo.put(uuid, asyncInfo);
        return uuid;
    }

    /**
     * 通过uuid获取进度信息
     *
     * @param uuid
     * @return
     */
    public static ImportAsyncInfo getAsyncInfo(String uuid) {
        return allAsyncInfo.get(uuid);
    }

    /**
     * 通过uuid删除对应的进度信息
     *
     * @param uuid
     * @return
     */
    public static void deleteAsyncInfo(String uuid) {
        allAsyncInfo.remove(uuid);
    }

    /**
     * uuid对应的进度 已处理的数据条数+1
     *
     * @param uuid
     */
    public static void doneSumAddOne(String uuid) {
        ImportAsyncInfo asyncInfo = getAsyncInfo(uuid);
        asyncInfo.setDoneSum(asyncInfo.getDoneSum() + 1);
    }

    /**
     * uuid对应的进度 失败的数据条数+1
     *
     * @param uuid
     */
    public static void errorSumAddOne(String uuid) {
        ImportAsyncInfo asyncInfo = getAsyncInfo(uuid);
        asyncInfo.setErrorSum(asyncInfo.getErrorSum() + 1);
    }

    /**
     * uuid对应的进度 成功的数据条数+1
     *
     * @param uuid
     */
    public static void successSumAddOne(String uuid) {
        ImportAsyncInfo asyncInfo = getAsyncInfo(uuid);
        asyncInfo.setSuccessSum(asyncInfo.getSuccessSum() + 1);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getTotality() {
        return totality;
    }

    public void setTotality(Integer totality) {
        this.totality = totality;
    }

    public Integer getDoneSum() {
        return doneSum;
    }

    public void setDoneSum(Integer doneSum) {
        this.doneSum = doneSum;
    }

    public Integer getErrorSum() {
        return errorSum;
    }

    public void setErrorSum(Integer errorSum) {
        this.errorSum = errorSum;
    }

    public Integer getSuccessSum() {
        return successSum;
    }

    public void setSuccessSum(Integer successSum) {
        this.successSum = successSum;
    }

    public String getErrorFilePath() {
        return errorFilePath;
    }

    public void setErrorFilePath(String errorFilePath) {
        this.errorFilePath = errorFilePath;
    }

    public Boolean getEnd() {
        return isEnd;
    }

    public void setEnd(Boolean end) {
        isEnd = end;
    }
}
