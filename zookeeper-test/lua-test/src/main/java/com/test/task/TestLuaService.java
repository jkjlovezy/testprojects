package com.test.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class TestLuaService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void callLua() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Boolean.class);

        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("test.lua")));
        List<String> keys = Arrays.asList("testLua", "hello lua");
        RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();
        Boolean execute =(Boolean) redisTemplate.execute(redisScript, stringRedisSerializer,stringRedisSerializer,keys, "100");
        log.info("===================execute:{}", execute);

/*        String key1 = "name";
        String value1="jkj";
        String key2="sex";
        String value2="jkj";
        String lua = "redis.call ('set', KEYS[1], ARGV[1]) \n"
                + "redis.call ('set', KEYS[2], ARGV[2]) \n "
                + " local str1 = redis.call ('get', KEYS [1]) \n "
                + " local str2 = redis.call ('get', KEYS [2]) \n "
                + " if str1 == str2 then \n "
                + " return 1 \n "
                + " end \n "
                + " return 0 \n ";
        DefaultRedisScript<Long> rs = new DefaultRedisScript<Long>();
        //设置脚本
//        rs.setScriptText(lua);
        rs.setScriptSource(new ResourceScriptSource(new ClassPathResource("test2.lua")));
        //定义返回类型。注意如果没有这个定义，spring不会返回结果
        rs.setResultType(Long.class);
        RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();

        //定义key
        List<String> keyList = new ArrayList<String>();
        keyList.add(key1);
        keyList.add(key2);
        Long result = (Long)redisTemplate.execute(rs,stringRedisSerializer,
                stringRedisSerializer,keyList,value1,value2);
        log.info("result: {}",result);*/
    }
}
