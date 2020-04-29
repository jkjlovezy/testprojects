package com.test;

import com.alibaba.fastjson.JSON;
import com.test.constants.UserStatusEnum;
import com.test.dao.GoodsDao;
import com.test.dao.UserDao;
import com.test.entity.User;
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
public class EnumTest {

    @Autowired
    private UserDao userDao;


    @Test
    public void enumTest() {
        log.info("-- enumTest --");
        User user = new User("jkj","234", UserStatusEnum.ENABLE);
        userDao.save(user);
        User u = userDao.findByUsername(user.getUsername());
        log.info("--user:{}",JSON.toJSONString(u));
    }


}
