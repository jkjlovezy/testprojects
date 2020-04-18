package com.jkj.test.transaction.defaults;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value="用户接口",tags = "用户",consumes="consumers",produces = "produces")
public class UserController {
    @Autowired
    private UserDao userDao;

    @GetMapping("/users")
    @ApiOperation(value = "用户列表", notes = "用户列表", httpMethod = "GET", tags = "用户")
    public List<User> getUser() {
        return userDao.findAll();
    }

    @GetMapping("/users/{id}")
    @ApiOperation(value = "用户详情", notes = "用户详情notes", httpMethod = "GET", tags = "用户")
    public User getUser(@PathVariable Long id) {
        return userDao.findOne(id);
    }
}
