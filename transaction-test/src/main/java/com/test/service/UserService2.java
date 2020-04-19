package com.test.service;

import com.test.dao.UserJdbcTemplateDao;
import com.test.entity.Goods;
import com.test.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService2 {

    @Autowired
    private GoodsService2 goodsService2;
    @Autowired
    private UserJdbcTemplateDao userJdbcTemplateDao;


    //-----nested-------
    @Transactional(transactionManager = "txManager")
    public void invokeNestedTrans() {
        userJdbcTemplateDao.save(buildUser());
        goodsService2.nestedTrans(buildGoods());
    }

    @Transactional(transactionManager = "txManager")
    public void invokeNestedTransAndSelfException() {
        userJdbcTemplateDao.save(buildUser());
        goodsService2.nestedTrans(buildGoods());
        int i = 1 / 0;
    }

    @Transactional(transactionManager = "txManager")
    public void invokeNestedTransAndChildException() {
        userJdbcTemplateDao.save(buildUser());
        goodsService2.nestedTransWithException(buildGoods());
    }

    @Transactional(transactionManager = "txManager")
    public void invokeNestedTransAndCatchChildException() {
        userJdbcTemplateDao.save(buildUser());
        try {
            goodsService2.nestedTransWithException(buildGoods());
        } catch (Exception e) {
            log.error("catch invoked method's exception: {}", e.getMessage());
        }
    }

    //-----nested-------


    private User buildUser() {
        User user = new User("jkj", "1234");
        return user;
    }

    private Goods buildGoods() {
        Goods goods = new Goods("apple", "fruit");
        return goods;
    }

}
