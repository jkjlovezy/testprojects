package com.test.hasAspect;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = {"classpath:aspect-in-trans.properties"}, inheritLocations = false)
@Slf4j
public class AspectInTransTest extends BaseTest {
    @Autowired
    private UserService userService;


    /**
     * 事务失效，Aspect在Transactional里面，切面逻辑优先执行，对异常进行捕获。
     */
    @Test
    public void withTrans() {
        log.info("-- withTrans --");
        userService.withTrans();
    }

}
