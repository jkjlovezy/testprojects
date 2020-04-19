package com.test.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "log-exception", havingValue = "true")
@Aspect
@Component
@Slf4j
@Order(1) //级别只要优先于@Transactional级别，在transaction逻辑外面
public class LogExceptionAspect {
    @Pointcut("execution(* com.test..*.*Trans*(..))")
    public void exceptionPointCut() {

    }

    @AfterThrowing(pointcut = "exceptionPointCut()", throwing = "e")
    public void afterThrowing(Exception e) {
    }

    @Around("exceptionPointCut()")
    public Object aroundServiceMethod(ProceedingJoinPoint pjr) throws Throwable {
        try {
            log.info("OutTransAspect around before");
            return pjr.proceed();
        } catch (Exception e) {
            log.error("LogExceptionAspect throws exception: {}", e.getMessage());
        } finally {
            log.info("OutTransAspect around after");
        }
        return null;
    }
}
