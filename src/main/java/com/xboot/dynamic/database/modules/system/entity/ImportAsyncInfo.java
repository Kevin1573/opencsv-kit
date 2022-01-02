package com.xboot.dynamic.database.modules.system.entity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于存储csv导入的进度信息
 */
public class ImportAsyncInfo {
    //用于存储所有的导入进度信息
    public static Map<String, ImportAsyncInfo> allAsyncInfo = new ConcurrentHashMap<>();

    //提示信息或 异常信息
    private String msg;
    //数据总数
    private AtomicInteger totality = new AtomicInteger(0);
    //已处理的数据条数
    private AtomicInteger doneSum = new AtomicInteger(0);
    //失败的数据条数
    private AtomicInteger errorSum = new AtomicInteger(0);
    //成功的数据条数
    private AtomicInteger successSum = new AtomicInteger(0);
    //错误文件的路径
    public String errorFilePath;
    //导入是否结束
    public AtomicBoolean isEnd = new AtomicBoolean(false);
    final private long beginTime = System.currentTimeMillis();
    private long costTime;

    /**
     * 创建一个进度信息,并获取对应的uuid
     *
     * @return
     */
    public static String createAsyncInfo() {
        ImportAsyncInfo asyncInfo = new ImportAsyncInfo();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //String uuid = "ea403a56-d863-4ba6-8faf-b516cbfd102f";
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
        asyncInfo.doneSum.addAndGet(1);
    }

    /**
     * uuid对应的进度 失败的数据条数+1
     *
     * @param uuid
     */
    public static void errorSumAddOne(String uuid) {
        ImportAsyncInfo asyncInfo = getAsyncInfo(uuid);
        asyncInfo.errorSum.addAndGet(1);
    }

    /**
     * uuid对应的进度 成功的数据条数+1
     *
     * @param uuid
     */
    public static void successSumAddOne(String uuid) {
        ImportAsyncInfo asyncInfo = getAsyncInfo(uuid);
        asyncInfo.successSum.addAndGet(1);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AtomicInteger getTotality() {
        return totality;
    }

    public void setTotality(Integer newTotality) {
        this.totality.set(newTotality);
    }

    public AtomicInteger getDoneSum() {
        return doneSum;
    }

    public void setDoneSum(Integer newDoneSum) {
        this.doneSum.set(newDoneSum);
    }

    public AtomicInteger getErrorSum() {
        return errorSum;
    }

    public void setErrorSum(Integer errorSum) {
        this.errorSum.set(errorSum);
    }

    public AtomicInteger getSuccessSum() {
        return successSum;
    }

    public void setSuccessSum(Integer successSum) {
        this.successSum.set(successSum);
    }

    public String getErrorFilePath() {
        return errorFilePath;
    }

    public void setErrorFilePath(String errorFilePath) {
        this.errorFilePath = errorFilePath;
    }

    public AtomicBoolean getEnd() {
        return isEnd;
    }

    public void setEnd(Boolean end) {
        isEnd.set(end);
    }

    public long getBeginTime() {
        return beginTime;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }
}
