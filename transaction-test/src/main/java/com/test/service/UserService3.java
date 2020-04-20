package com.test.service;

import com.test.dao.UserDao;
import com.test.dao.UserJdbcTemplateDao;
import com.test.entity.Goods;
import com.test.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService3 {

    @Autowired
    private GoodsService3 goodsService3;
    @Autowired
    private UserJdbcTemplateDao userJdbcTemplateDao;

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private UserDao userDao;

    //-----jdbc use JPA TX Manager-------
    @Transactional()
    public void jpaTxManager() {
        userJdbcTemplateDao.save(buildUser());
        goodsService3.requiredTrans(buildGoods());
        int i = 1 / 0;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void jpaTxManagerInvokeDsTxManager() {
        userJdbcTemplateDao.save(buildUser());
        goodsService3.requiresNewTransWithDsTM(buildGoods());
        int i = 1 / 0;
    }
    //-----jdbc use JPA TX Manager-------


    private User buildUser() {
        User user = new User("jkj", "1234");
        return user;
    }

    private Goods buildGoods() {
        Goods goods = new Goods("apple", "fruit");
        return goods;
    }

}
