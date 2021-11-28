package com.xboot.dynamic.database.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_data")
public class TData {
    private int id;
    private String name;
}
