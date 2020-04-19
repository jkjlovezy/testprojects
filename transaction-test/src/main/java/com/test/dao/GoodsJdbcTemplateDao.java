package com.test.dao;

import com.test.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GoodsJdbcTemplateDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(Goods goods) {
        jdbcTemplate.update("insert into goods(name,type) values(?,?)", goods.getName(), goods.getType());
    }


}
