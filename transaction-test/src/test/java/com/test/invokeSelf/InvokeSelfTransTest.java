package com.test.invokeSelf;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class InvokeSelfTransTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 事务失效，调用自己内部的事务方法，方法没有被事务增强。
     */
    @Test
    public void invokeSelfTrans() {
        log.info("-- invokeSelfTrans --");
        userService.invokeSelfTrans();
    }


}
