package com.xboot.dynamic.database.modules.system.controller;


import com.xboot.dynamic.database.modules.system.entity.User;
import com.xboot.dynamic.database.modules.system.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xboot
 * @since 2021-09-26
 */
@RestController
@RequestMapping("/system/user")
public class UserController {

    @Resource
    private IUserService userService;

    @GetMapping("/")
    public List<User> list() {
        return userService.getAlls();
    }

    @PostMapping("/")
    public Boolean post(@RequestBody User user) {
        boolean save = userService.save(user);
        return save;
    }
}

