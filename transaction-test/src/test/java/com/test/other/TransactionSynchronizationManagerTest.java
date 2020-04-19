package com.test.other;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class TransactionSynchronizationManagerTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 期望事务方法中的 部分逻辑 在事务提交后 再执行。
     */
    @Test
    public void commitBeforeIoOperation() {
        log.info("-- commitBeforeIoOperation --");
        userService.commitBeforeIoOperation();
    }


}
