package com.test.propagation;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * nested：如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则进行与PROPAGATION_REQUIRED类似的操作。
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class NestedTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 调用嵌套方法时报错：
     * org.springframework.transaction.NestedTransactionNotSupportedException: JpaDialect does not support savepoints - check your JPA provider's capabilities
     * Hibernate/JPA并不能实现嵌套事务，嵌套事务仅仅在JDBC级别支持，对于Hibernate/JPA要实现嵌套事务，也仅仅在dialect为Oracle的情况下才完全实现
     */
    @Test
    public void invokeNestedTrans() {
        log.info("-- invokeNestedTrans --");
        userService.invokeNestedTrans();
    }

}
