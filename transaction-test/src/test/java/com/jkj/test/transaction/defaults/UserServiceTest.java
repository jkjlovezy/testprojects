package com.jkj.test.transaction.defaults;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {
    private final static Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GoodsDao goodsDao;

    @Before
    public void before() {
        userDao.deleteAll();
        goodsDao.deleteAll();
        logger.info("before user={}", JSON.toJSONString(userDao.findAll()));
        logger.info("before goods={}", JSON.toJSONString(goodsDao.findAll()));
    }

    @After
    public void after() {
        logger.info("after user={}", JSON.toJSONString(userDao.findAll()));
        logger.info("after goods={}", JSON.toJSONString(goodsDao.findAll()));
    }

    @Test
    public void save() {
        logger.info("-- save --");
        userService.save();
    }

    @Test
    public void save2() {
        logger.info("-- save2 --");
        userService.save2();
    }

    @Test
    public void save3() {
        logger.info("-- save3 --");
        userService.save3();
    }

    @Test
    public void save4() {
        logger.info("-- save4 --");
        userService.save4();
    }

    @Test
    public void save5() {
        logger.info("-- save5 --");
        userService.save4();
    }


}
