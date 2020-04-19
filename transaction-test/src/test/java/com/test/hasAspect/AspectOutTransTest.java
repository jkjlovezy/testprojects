package com.test.hasAspect;

import com.test.BaseTest;
import com.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = {"classpath:aspect-out-trans.properties"}, inheritLocations = false)
@Slf4j
public class AspectOutTransTest extends BaseTest {
    @Autowired
    private UserService userService;

    /**
     * 事务生效，Aspect在Transactional外面，事务逻辑优先执行，进行了回退。
     */
    @Test
    public void withTrans() {
        log.info("-- withTrans --");
        userService.withTrans();
    }

}
