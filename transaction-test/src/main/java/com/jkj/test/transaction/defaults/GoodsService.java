package com.jkj.test.transaction.defaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    public Goods findByNname(String username) {
        return goodsDao.findByName(username);
    }

    public Goods save(Goods goods) {
        return goodsDao.save(goods);
    }


}
