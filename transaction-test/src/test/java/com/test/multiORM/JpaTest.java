package com.test.multiORM;

import com.test.BaseTest;
import com.test.service.UserService3;
import com.test.service.UserService4;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 *
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class JpaTest extends BaseTest {
    @Autowired
    private UserService4 userService4;

    /**
     * 注意：
     * jpa事务需用自己的JpaTransactionManager。
     * 报错：NESTED | REQUIRES_NEW | REQUIRED
     * 报不存在事务，MANDATORY
     * 不报错 SUPPORTS | NOT_SUPPORTED | NEVER
     */
    @Test
    public void xxTransWithDsTxManager() {
        log.info("-- xxTransWithDsTxManager --");
        userService4.xxTransWithDsTxManager();
    }

    /**
     * 注意：
     * 调用方式： @Transaction(jpaTM) hasException -> @Transaction( dsTM，requires_new).  子事务提交，但父事务没有按照期望进行回滚。
     */
    @Test
    public void jpaTxManagerInvokeDsTxManager() {
        log.info("-- jpaTxManagerInvokeDsTxManager --");
        userService4.jpaTxManagerInvokeDsTxManager();
    }

    /**
     * 调用方式： @Transaction(jpaTM) hasException -> @Transaction( jpaTM，requires_new).  父回滚，子提交。
     */
    @Test
    public void jpaTxManagerInvokeJpaTxManager() {
        log.info("-- jpaTxManagerInvokeJpaTxManager --");
        userService4.jpaTxManagerInvokeJpaTxManager();
    }

}
