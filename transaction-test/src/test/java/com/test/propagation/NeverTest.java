package com.test.propagation;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * never：以非事务方式执行，如果当前存在事务，则抛出异常。
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class NeverTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 在调用嵌套方法时，报错：
     * org.springframework.transaction.IllegalTransactionStateException: Existing transaction found for transaction marked with propagation 'never'.
     */
    @Test
    public void invokeNeverTrans() {
        log.info("-- invokeNeverTrans --");
        userService.invokeNeverTrans();
    }


}
