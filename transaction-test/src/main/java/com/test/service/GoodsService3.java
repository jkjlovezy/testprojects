package com.test.service;

import com.test.dao.GoodsJdbcTemplateDao;
import com.test.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodsService3 {
    @Autowired
    private GoodsJdbcTemplateDao goodsJdbcTemplateDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void requiredTrans(Goods goods) {
        this.goodsJdbcTemplateDao.save(goods);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,transactionManager = "txManager")
    public void requiresNewTransWithDsTM(Goods goods) {
        this.goodsJdbcTemplateDao.save(goods);
    }


}
