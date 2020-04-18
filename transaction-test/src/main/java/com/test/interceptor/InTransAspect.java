package com.test.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "aspect-in-trans", havingValue = "true")
@Aspect
@Component
@Slf4j
@Order //配置@Order且默认值是LOWEST_PRECEDENCE = Integer.MAX_VALUE | 不配置@Order注解， 在transaction里面
public class InTransAspect {
    @Pointcut("execution(* com.test..*.*Trans*(..))")
    public void exceptionPointCut() {

    }

    @AfterThrowing(pointcut = "exceptionPointCut()", throwing = "e")
    public void afterThrowing(Exception e) {
    }

    @Around("exceptionPointCut()")
    public Object aroundServiceMethod(ProceedingJoinPoint pjr) throws Throwable {
        try {
            log.info("InTransAspect around before");
            return pjr.proceed();
        } catch (Exception e) {
            log.error("InTransAspect throws exception: {}", e.getMessage());
        } finally {
            log.info("InTransAspect around after");
        }
        return null;
    }
}
