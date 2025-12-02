package com.example.minion.aop;

import com.example.minion.dto.CrackRequest;
import com.example.minion.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Value("${minion.id}")
    private String minionId;

    @Around("execution(* com.example.minion.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        
        try {
            initializeMDCIfNeeded(args);
            
            log.info("Controller method {} called with args: {}", methodName, args);
            
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("Controller method {} completed in {}ms with result: {}",
                    methodName, executionTime, result);
            
            return result;
        } catch (Exception e) {
            log.error("Controller method {} failed: {}", methodName, e.getMessage());
            throw e;
        } finally {
            LogUtils.clearMDC();
        }
    }
    
    private void initializeMDCIfNeeded(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof CrackRequest) {
                CrackRequest request = (CrackRequest) arg;
                LogUtils.setTaskId(request.getTaskId());
                LogUtils.setMinionId(minionId);
                break;
            }
        }
    }
}