package com.test.propagation;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * not_supported：以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class NotSupportedTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 嵌套方法不在事务中，执行成功；调用方法事务提交。
     */
    @Test
    public void invokeNotSupportedTrans() {
        log.info("-- invokeNotSupportedTrans --");
        userService.invokeNotSupportedTrans();
    }

    /**
     * 嵌套方法不在事务中，抛出异常前的SQL执行成功； 调用方法事务回滚。
     */
    @Test
    public void invokeNotSupportedTransAndChildException() {
        log.info("-- invokeNotSupportedTransAndChildException --");
        userService.invokeNotSupportedTransAndChildException();
    }

    /**
     * 嵌套方法不在事务中，抛出异常前的insert数据成功； 调用方法对嵌套方法的异常进行了捕获，事务提交。
     */
    @Test
    public void invokeNotSupportedTransAndCatchChildException() {
        log.info("-- invokeNotSupportedTransAndCatchChildException --");
        userService.invokeNotSupportedTransAndCatchChildException();
    }


}
