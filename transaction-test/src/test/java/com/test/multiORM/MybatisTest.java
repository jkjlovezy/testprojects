package com.test.multiORM;

import com.alibaba.fastjson.JSON;
import com.test.TransactionTestApplication2;
import com.test.dao.GoodsDao;
import com.test.dao.UserDao;
import com.test.service.UserService5;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionTestApplication2.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class MybatisTest {
    @Autowired
    private UserService5 userService5;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GoodsDao goodsDao;

    /**
     * 每个用例运行前，销毁数据。
     */
    @Before
    public void before() {
        userDao.deleteAll();
        goodsDao.deleteAll();
//        log.info("init data: user={},goods={}", JSON.toJSONString(userDao.findAll()), JSON.toJSONString(goodsDao.findAll()));
    }

    /**
     * 每个用例运行后，打印当前数据信息。
     */
    @After
    public void after() {
        log.info("current data: user={},goods={}", JSON.toJSONString(userDao.findAll()), JSON.toJSONString(goodsDao.findAll()));
    }

    /**
     * 用DsTransactionManager，事务回滚。
     */
    @Test
    public void dsTxManager() {
        log.info("-- dsTxManager --");
        userService5.dsTxManager();
    }

    /**
     * 用JpaTransactionMnager，事务回滚。
     */
    @Test
    public void jpaTxManager() {
        log.info("-- jpaTxManager --");
        userService5.jpaTxManager();
    }




}
