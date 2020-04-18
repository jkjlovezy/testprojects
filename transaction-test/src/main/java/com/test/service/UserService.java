package com.test.service;

import com.test.dao.GoodsDao;
import com.test.dao.UserDao;
import com.test.entity.Goods;
import com.test.entity.User;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private UserService userService;

    public void nonTrans() {
        userDao.save(buildUser());
        int i = 1 / 0;
        goodsDao.save(buildGoods());
    }

    @Transactional
    public void withTrans() {
        userDao.save(buildUser());
        int i = 1 / 0;
        goodsDao.save(buildGoods());
    }

    public void invokeSelfTrans() {
        withTrans();
    }

    public void invokeSelfTransByAopContext() {
        ((UserService) AopContext.currentProxy()).withTrans();
    }

    public void invokeSelfTransByAutowired() {
        userService.withTrans();
    }


    private User buildUser() {
        User user = new User("jkj", "1234");
        return user;
    }

    private Goods buildGoods() {
        Goods goods = new Goods("apple", "fruit");
        return goods;
    }

}
