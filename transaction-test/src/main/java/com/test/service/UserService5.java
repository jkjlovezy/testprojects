package com.test.service;

import com.test.dao.UserDao;
import com.test.entity.Goods;
import com.test.entity.User;
import com.test.mybatis.dao.MybatisUserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService5 {

    @Autowired
    private GoodsService4 goodsService4;

    @Autowired
    private MybatisUserDao mybatisUserDao;


    @Transactional(transactionManager = "txManager")
    public void dsTxManager() {
        mybatisUserDao.insert(buildUser());
        int i = 1 / 0;
    }

    @Transactional(transactionManager = "transactionManager")
    public void jpaTxManager() {
        mybatisUserDao.insert(buildUser());
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
