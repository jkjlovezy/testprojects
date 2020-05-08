package com.focustech.gateway.site.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class TestLuaService {
    @Resource
    private DefaultRedisScript<Boolean> redisScript;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void callLua(){
        redisScript.setResultType(Boolean.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("test.lua")));
        List<String> keys = Arrays.asList("testLua", "hello lua");
        Boolean execute = stringRedisTemplate.execute(redisScript, keys, "100");
        log.info("===================execute:{}",execute);
    }
}
