package com.test.propagation;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * supports：支持当前事务，如果当前没有事务，就以非事务方式执行。
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class SupportsTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 嵌套方法用调用方法的事务，事务回滚。
     */
    @Test
    public void invokeSupportsTransAndSelfException() {
        log.info("-- invokeSupportsTransAndSelfException --");
        userService.invokeSupportsTransAndSelfException();
    }

    /**
     * 没有事务，抛出异常前的SQL都会执行成功。
     */
    @Test
    public void noneInvokeSupportsTransAndSelfException() {
        log.info("-- noneInvokeSupportsTransAndSelfException --");
        userService.noneInvokeSupportsTransAndSelfException();
    }


}
