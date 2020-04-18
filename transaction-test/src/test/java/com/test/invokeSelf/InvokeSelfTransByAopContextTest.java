package com.test.invokeSelf;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class InvokeSelfTransByAopContextTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 事务生效，调用类自己内部的事务方法，调用的是proxy方法。
     */
    @Test
    public void selfInvokeTransByAopContext() {
        log.info("-- invokeSelfTransByAopContext --");
        userService.invokeSelfTransByAopContext();
    }


}
