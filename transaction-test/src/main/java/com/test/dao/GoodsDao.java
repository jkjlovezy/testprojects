package com.test.dao;

import com.test.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsDao extends JpaRepository<Goods, Long> {
    Goods findByName(String name);

    Goods save(Goods goods);
}
