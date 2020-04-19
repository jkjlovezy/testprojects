package com.test.propagation;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * required：支持当前事务，如果当前没有事务，就新建一个事务。此项也是默认配置
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class RequiredTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 嵌套方法抛出异常，事务回滚。
     */
    @Test
    public void invokeRequiredTransAndChildException() {
        log.info("-- invokeRequiredTransAndChildException --");
        userService.invokeRequiredTransAndChildException();
    }

    /**
     * 调用方法抛出异常，事务回滚。
     */
    @Test
    public void invokeRequiredTransAndSelfException() {
        log.info("-- invokeRequiredTransAndSelfException --");
        userService.invokeRequiredTransAndSelfException();
    }

    /**
     * 嵌套方法抛出异常，调用方法中进行了捕获。调用方法提交事务时报错:
     * org.springframework.transaction.TransactionSystemException: Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Transaction marked as rollbackOnly
     * 原因：被调用方法由于抛出异常，事务已被标记为rollbackOnly，调用方法不能再提交此事务。
     */
    @Test
    public void invokeRequiredTransAndCatchChildException() {
        log.info("-- invokeRequiredTransAndCatchChildException --");
        userService.invokeRequiredTransAndCatchChildException();
    }


}
