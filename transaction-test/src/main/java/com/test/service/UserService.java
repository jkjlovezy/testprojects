package com.test.service;

import com.test.dao.GoodsDao;
import com.test.dao.UserDao;
import com.test.entity.Goods;
import com.test.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GoodsDao goodsDao;


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

    //-----required-------
    @Transactional
    public void invokeRequiredTransAndSelfException() {
        userDao.save(buildUser());
        goodsService.requiredTrans(buildGoods());
        int i = 1 / 0;
    }

    @Transactional
    public void invokeRequiredTransAndChildException() {
        userDao.save(buildUser());
        goodsService.requiredTransWithException(buildGoods());
    }

    @Transactional
    public void invokeRequiredTransAndCatchChildException() {
        userDao.save(buildUser());
        try {
            goodsService.requiredTransWithException(buildGoods());
        } catch (Exception e) {
            log.error("catch invoked method's exception: {}", e.getMessage());
        }
    }

    //-----required-------

    //-----requires_new-------
    @Transactional
    public void invokeRequiresNewTransAndSelfException() {
        userDao.save(buildUser());
        goodsService.requiresNewTrans(buildGoods());
        int i = 1 / 0;
    }

    @Transactional
    public void invokeRequiresNewTransAndChildException() {
        userDao.save(buildUser());
        goodsService.requiresNewTransWithException(buildGoods());
    }

    @Transactional
    public void invokeRequiresNewTransAndCatchChildException() {
        userDao.save(buildUser());
        try {
            goodsService.requiresNewTransWithException(buildGoods());
        } catch (Exception e) {
            log.error("catch invoked method's exception: {}", e.getMessage());
        }
    }

    //-----requires_new-------


    //-----never-------
    @Transactional
    public void invokeNeverTrans() {
        userDao.save(buildUser());
        goodsService.neverTrans(buildGoods());
    }
    //-----never-------


    //-----mandatory-------
    @Transactional
    public void invokeMandatoryTrans() {
        userDao.save(buildUser());
        goodsService.mandatoryTrans(buildGoods());
    }

    @Transactional
    public void invokeMandatoryTransAndSelfException() {
        userDao.save(buildUser());
        goodsService.mandatoryTrans(buildGoods());
        int i = 1 / 0;
    }

    public void noneInvokeMandatoryTrans() {
        userDao.save(buildUser());
        goodsService.mandatoryTrans(buildGoods());
    }
    //-----mandatory-------


    //-----not_supported-------
    @Transactional
    public void invokeNotSupportedTrans() {
        userDao.save(buildUser());
        goodsService.notSupportedTrans(buildGoods());
    }

    @Transactional
    public void invokeNotSupportedTransAndChildException() {
        userDao.save(buildUser());
        goodsService.notSupportedTransWithException(buildGoods());
    }

    @Transactional
    public void invokeNotSupportedTransAndCatchChildException() {
        userDao.save(buildUser());
        try {
            goodsService.notSupportedTransWithException(buildGoods());
        } catch (Exception e) {
            log.error("catch invoked method's exception: {}", e.getMessage());
        }
    }
    //-----not_supported-------


    //-----supports-------
    @Transactional
    public void invokeSupportsTransAndSelfException() {
        userDao.save(buildUser());
        goodsService.supportsTrans(buildGoods());
        int i = 1 / 0;
    }

    public void noneInvokeSupportsTransAndSelfException() {
        userDao.save(buildUser());
        goodsService.supportsTrans(buildGoods());
        int i = 1 / 0;
    }
    //-----supports-------

    //-----nested-------
    @Transactional
    public void invokeNestedTrans() {
        userDao.save(buildUser());
        goodsService.nestedTrans(buildGoods());
    }
    //-----nested-------

    @Transactional
    public void commitBeforeIoOperation() {
        userDao.save(buildUser());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                int i = 1 / 0;
                //异步IO操作
            }
        });

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
