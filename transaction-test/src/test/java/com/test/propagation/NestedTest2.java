package com.test.propagation;

import com.test.BaseTest;
import com.test.service.UserService2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * nested：如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则进行与PROPAGATION_REQUIRED类似的操作。
 * 依赖父事务，父事务提交才提交，父事务若回滚子事务也进行回滚。
 */
@TestPropertySource(locations = {"classpath:application.properties"}, inheritLocations = false)
@Slf4j
public class NestedTest2 extends BaseTest {
    @Autowired
    private UserService2 userService2;


    /**
     * A->B，A事务提交，B事务依赖于A也进行提交。
     */
    @Test
    public void invokeNestedTrans2() {
        log.info("-- invokeNestedTrans2 --");
        userService2.invokeNestedTrans();
    }

    /**
     * A->B，A有异常。A事务回滚，B事务是Nested，依赖于A也进行回滚。
     */
    @Test
    public void invokeNestedTransAndSelfException() {
        log.info("-- invokeNestedTransAndSelfException --");
        userService2.invokeNestedTransAndSelfException();
    }

    /**
     * A->B，B有异常，导致A也异常。A事务回滚，B事务回滚。
     */
    @Test
    public void invokeNestedTransAndChildException() {
        log.info("-- invokeNestedTransAndChildException --");
        userService2.invokeNestedTransAndChildException();
    }

    /**
     * A->B，B有异常，A进行了捕获。A事务提交，B事务回滚。
     */
    @Test
    public void invokeNestedTransAndCatchChildException() {
        log.info("-- invokeNestedTransAndCatchChildException --");
        userService2.invokeNestedTransAndCatchChildException();
    }


}
