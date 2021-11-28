package com.xboot.dynamic.database.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xboot.dynamic.database.modules.system.entity.TData;

import java.util.List;

public interface ICSVService extends IService<TData> {
    List<TData> getAlls(List<Integer> ids);
}
