package com.test.service;

import com.test.dao.UserDao;
import com.test.dao.UserJdbcTemplateDao;
import com.test.entity.Goods;
import com.test.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService4 {

    @Autowired
    private GoodsService4 goodsService4;

    @Autowired
    private UserDao userDao;

    //-----jpa use DS TX Manager-------
    @Transactional(transactionManager = "txManager", propagation = Propagation.SUPPORTS)
    public void xxTransWithDsTxManager() {
        userDao.save(buildUser());
        int i = 1 / 0;
    }

    @Transactional(transactionManager = "txManager")
    public void dsTxManagerInvokeJpaTxManager() {
        userDao.save(buildUser());
        goodsService4.requiredTransWithJpaTxManager(buildGoods());
        int i = 1 / 0;
    }

    @Transactional()
    public void jpaTxManagerInvokeDsTxManager() {
        userDao.save(buildUser());
        goodsService4.requiresNewTransWithDsTxManager(buildGoods());
        int i = 1 / 0;
    }

    @Transactional()
    public void jpaTxManagerInvokeJpaTxManager() {
        userDao.save(buildUser());
        goodsService4.requiresNewTransWithJpaTxManager(buildGoods());
        int i = 1 / 0;
    }


    //-----jpa use DS TX Manager-------


    private User buildUser() {
        User user = new User("jkj", "1234");
        return user;
    }

    private Goods buildGoods() {
        Goods goods = new Goods("apple", "fruit");
        return goods;
    }

}
