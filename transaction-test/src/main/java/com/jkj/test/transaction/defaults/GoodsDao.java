package com.jkj.test.transaction.defaults;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsDao extends JpaRepository<Goods, Long> {
    Goods findByName(String name);

    Goods save(Goods goods);
}
