package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@Slf4j
public class PerformanceAspect {
    //Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);
    //no need logger obj we put @Slf4j

//As an expression we use not execution but  @Annotation because I created custom annotation for this reason.
    //give custom annotation address as a parameter

    @Pointcut("@annotation(com.cydeo.annotation.ExecutionTime)")
    private void anyExecutionTimeOperation() {}

    @Around("anyExecutionTimeOperation()")
    public Object anyExecutionTimeAdvice(ProceedingJoinPoint proceedingJoinPoint) {

    //below for measuring execution time
        long beforeTime = System.currentTimeMillis();
        //Result is going to be an object

        Object result=null;
        log.info("Execution will start");
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        long afterTime = System.currentTimeMillis();
        log.info("Time taken to execute :  {} ms - Method: {} - Parameters:{}", (afterTime-beforeTime), proceedingJoinPoint.getSignature().toShortString(), proceedingJoinPoint.getArgs());
        return result;
    }
}
