package com.test;

import com.alibaba.fastjson.JSON;
import com.test.dao.GoodsDao;
import com.test.dao.UserDao;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({"classpath:application.properties", "classpath:log-exception.properties"})
@Slf4j
public class BaseTest {
    @Autowired
    private UserService userService;
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
     * 无事务，不会回退。
     */
    @Test
    public void nonTrans() {
        log.info("-- nonTrans --");
        userService.nonTrans();
    }

    /**
     * 有事务，会回退。
     */
    @Test
    public void withTrans() {
        log.info("-- withTrans --");
        userService.withTrans();
    }

}
