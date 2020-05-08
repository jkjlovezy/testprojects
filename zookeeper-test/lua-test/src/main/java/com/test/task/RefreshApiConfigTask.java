package com.test.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefreshApiConfigTask {
    @Autowired
    TestLuaService testLuaService;


    @Scheduled(cron = "0/5 * * * * ?")
    public void testLua() {
        log.info("---task begin testLua");
        testLuaService.callLua();
    }
}
