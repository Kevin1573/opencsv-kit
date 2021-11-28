package com.xboot.dynamic.database.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xboot.dynamic.database.modules.system.entity.TData;
import com.xboot.dynamic.database.modules.system.mapper.CSVImportMapper;
import com.xboot.dynamic.database.modules.system.service.ICSVService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CSVServiceImpl extends ServiceImpl<CSVImportMapper, TData> implements ICSVService {
    @Override
    public List<TData> getAlls(List<Integer> ids) {
        QueryChainWrapper<TData> query = query();
        query.in("id", ids);
        List<TData> list = query.list();
        return list;
    }
}
