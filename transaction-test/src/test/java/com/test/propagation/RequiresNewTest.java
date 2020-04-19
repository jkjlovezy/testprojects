package com.test.propagation;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * requires_new：新建事务，如果当前存在事务，把当前事务挂起。
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class RequiresNewTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 调用方法抛出异常，调用方法事务回滚，嵌套方法事务提交。
     */
    @Test
    public void invokeRequiresNewTransAndSelfException() {
        log.info("-- invokeRequiresNewTransAndSelfException --");
        userService.invokeRequiresNewTransAndSelfException();
    }

    /**
     * 嵌套方法抛出异常，两个方法的事务都回滚。
     */
    @Test
    public void invokeRequiresNewTransAndChildException() {
        log.info("-- invokeRequiresNewTransAndChildException --");
        userService.invokeRequiresNewTransAndChildException();
    }

    /**
     * 嵌套法抛出异常，调用方法中进行了捕获。调用方法事务提交，嵌套方法事务回滚。
     */
    @Test
    public void invokeRequiresNewTransAndCatchChildException() {
        log.info("-- invokeRequiresNewTransAndCatchChildException --");
        userService.invokeRequiresNewTransAndCatchChildException();
    }


}
