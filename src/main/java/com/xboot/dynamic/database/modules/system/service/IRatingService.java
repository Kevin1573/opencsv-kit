package com.xboot.dynamic.database.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xboot.dynamic.database.modules.system.entity.Ratings;
import com.xboot.dynamic.database.modules.system.entity.TData;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public interface IRatingService extends IService<Ratings> {
    Map<String, String> importAndExportData(InputStreamReader isr, String s, String uuid);
}
