package com.test.multiORM;

import com.test.BaseTest;
import com.test.service.UserService;
import com.test.service.UserService3;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class JdbcTemplateTest extends BaseTest {
    @Autowired
    private UserService3 userService3;


    /**
     * 事务回滚，jdbcTemplate用JpaTransactionManager可正常工作。
     */
    @Test
    public void jpaTxManager() {
        log.info("-- jpaTxManager --");
        userService3.jpaTxManager();
    }

    /**
     * 调用方式： @Transaction(jpaTM) hasException -> @Transaction(dsTM，requires_new).  子事务提交，但父事务没有按照期望进行回滚。
     */
    @Test
    public void jpaTxManagerInvokeDsTxManager() {
        log.info("-- jpaTxManagerInvokeDsTxManager --");
        userService3.jpaTxManagerInvokeDsTxManager();
    }


}
