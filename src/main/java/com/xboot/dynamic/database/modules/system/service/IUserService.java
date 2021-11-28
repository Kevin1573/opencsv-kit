package com.xboot.dynamic.database.modules.system.service;

import com.xboot.dynamic.database.modules.system.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xboot
 * @since 2021-09-26
 */
public interface IUserService extends IService<User> {
    List<User> getAlls();
}
