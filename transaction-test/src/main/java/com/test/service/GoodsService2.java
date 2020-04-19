package com.test.service;

import com.test.dao.GoodsJdbcTemplateDao;
import com.test.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodsService2 {
    @Autowired
    private GoodsJdbcTemplateDao goodsJdbcTemplateDao;

    @Transactional(propagation = Propagation.NESTED, transactionManager = "txManager")
    public void nestedTrans(Goods goods) {
        this.goodsJdbcTemplateDao.save(goods);
    }

    @Transactional(propagation = Propagation.NESTED, transactionManager = "txManager")
    public void nestedTransWithException(Goods goods) {
        this.goodsJdbcTemplateDao.save(goods);
        int i = 1 / 0;
    }

}
