package com.test.propagation;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * mandatory：支持当前事务，如果当前没有事务，就抛出异常。
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class MandatoryTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 事务提交
     */
    @Test
    public void invokeMandatoryTrans() {
        log.info("-- invokeMandatoryTrans --");
        userService.invokeMandatoryTrans();
    }

    /**
     * 调用方法自己抛出异常， 调用与嵌套方法共用的同一个事务回滚。
     */
    @Test
    public void invokeMandatoryTransAndSelfException() {
        log.info("-- invokeMandatoryTransAndSelfException --");
        userService.invokeMandatoryTransAndSelfException();
    }

    /**
     * 调用方法没有事务，在执行嵌套方法时报错：
     * org.springframework.transaction.IllegalTransactionStateException: No existing transaction found for transaction marked with propagation 'mandatory'
     */
    @Test
    public void noneInvokeMandatoryTrans() {
        log.info("-- noneInvokeMandatoryTrans --");
        userService.noneInvokeMandatoryTrans();
    }


}
