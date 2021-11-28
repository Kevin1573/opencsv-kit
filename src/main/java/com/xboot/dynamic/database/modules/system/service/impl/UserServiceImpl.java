package com.xboot.dynamic.database.modules.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.xboot.dynamic.database.modules.system.entity.User;
import com.xboot.dynamic.database.modules.system.mapper.UserMapper;
import com.xboot.dynamic.database.modules.system.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xboot
 * @since 2021-09-26
 */
@Service
@DS("slave")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @DS("slave")
    public List<User> getAlls() {
        QueryChainWrapper<User> query = query();
        List<User> userList = query.list();
//        List<User> users = this.baseMapper.selectBatchIds(Arrays.asList("1"));
        return userList;
    }

}
