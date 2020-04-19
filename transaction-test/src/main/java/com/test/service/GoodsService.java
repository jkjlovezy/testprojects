package com.test.service;

import com.test.dao.GoodsDao;
import com.test.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void requiredTrans(Goods goods) {
        goodsDao.save(goods);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void requiredTransWithException(Goods goods) {
        goodsDao.save(goods);
        int i = 1 / 0;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresNewTrans(Goods goods) {
        goodsDao.save(goods);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresNewTransWithException(Goods goods) {
        goodsDao.save(goods);
        int i = 1 / 0;
    }

    @Transactional(propagation = Propagation.NEVER)
    public void neverTrans(Goods goods) {
        goodsDao.save(goods);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void mandatoryTrans(Goods goods) {
        goodsDao.save(goods);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void notSupportedTrans(Goods goods) {
        goodsDao.save(goods);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void notSupportedTransWithException(Goods goods) {
        goodsDao.save(goods);
        int i = 1 / 0;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public void supportsTrans(Goods goods) {
        goodsDao.save(goods);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void nestedTrans(Goods goods) {
        goodsDao.save(goods);
    }


}
