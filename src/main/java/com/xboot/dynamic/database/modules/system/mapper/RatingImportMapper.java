package com.xboot.dynamic.database.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xboot.dynamic.database.modules.system.entity.Ratings;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RatingImportMapper extends BaseMapper<Ratings> {

    List<Ratings> selectPageA(@Param("batchNo") String batchNo,
            @Param("current") int current, @Param("pageSize") int pageSize);

    int batchInsertImportData(@Param("ratingsList") List<Ratings> ratingsList);
}
