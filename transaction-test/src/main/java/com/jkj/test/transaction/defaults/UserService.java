package com.jkj.test.transaction.defaults;

import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
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

    public void save() {
        userDao.save(buildUser());
        int i = 1 / 0;
        goodsDao.save(buildGoods());
    }

    @Transactional
    public void save2() {
        userDao.save(buildUser());
        int i = 1 / 0;
        goodsDao.save(buildGoods());
    }

    public void save3() {
        save2();
    }

    public void save4() {
        ((UserService) AopContext.currentProxy()).save2();
    }

    public void save5() {
        userService.save2();
    }

    @Transactional
    public void save6() {
        save2();
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
