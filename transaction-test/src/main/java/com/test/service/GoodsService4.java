package com.test.service;

import com.test.dao.GoodsDao;
import com.test.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodsService4 {

    @Autowired
    private GoodsDao goodsDao;

    @Transactional()
    public void requiredTransWithJpaTxManager(Goods goods) {
        goodsDao.save(goods);
    }

    @Transactional(transactionManager = "txManager",propagation = Propagation.REQUIRES_NEW)
    public void requiresNewTransWithDsTxManager(Goods goods) {
        goodsDao.save(goods);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresNewTransWithJpaTxManager(Goods goods) {
        goodsDao.save(goods);
    }

}
