package com.example.master.aop;

import com.example.master.dto.BatchResponse;
import com.example.master.dto.ResultRequest;
import com.example.master.dto.TaskResponse;
import com.example.master.entity.BatchEntity;
import com.example.master.entity.SubTaskEntity;
import com.example.master.entity.TaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/**
 * Logging aspect that adds structured logging with MDC for all service methods.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.master..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();
        
        try {
            extractAndSetMDCFromArguments(joinPoint.getArgs());
            
            log.debug("Starting method: {} with args: {}", methodName, Arrays.toString(joinPoint.getArgs()));
            
            Object result = joinPoint.proceed();
            
            extractAndSetMDCFromReturnValue(result);
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("Completed method: {} in {}ms, result: {}", methodName, executionTime, result);
            
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Error in method: {} after {}ms - {}", methodName, executionTime, e.getMessage(), e);
            throw e;
        } finally {
            // Clear MDC after method execution
            MDC.clear();
        }
    }

    private void extractAndSetMDCFromArguments(Object[] args) {
        if (args == null) return;
        
        for (Object arg : args) {
            if (arg == null) continue;
            
            // Extract from DTOs
            if (arg instanceof ResultRequest) {
                ResultRequest req = (ResultRequest) arg;
                setMDCIfNotNull("subTaskId", req.getTaskId()); // taskId in ResultRequest is actually subTaskId
            } else if (arg instanceof BatchResponse) {
                BatchResponse resp = (BatchResponse) arg;
                setMDCIfNotNull("batchId", resp.getBatchId());
            } else if (arg instanceof TaskResponse) {
                TaskResponse resp = (TaskResponse) arg;
                setMDCIfNotNull("taskId", resp.getTaskId());
            }
            // Extract from Entities
            else if (arg instanceof BatchEntity) {
                BatchEntity entity = (BatchEntity) arg;
                setMDCIfNotNull("batchId", entity.getBatchId() != null ? entity.getBatchId().toString() : null);
            } else if (arg instanceof TaskEntity) {
                TaskEntity entity = (TaskEntity) arg;
                setMDCIfNotNull("taskId", entity.getTaskId() != null ? entity.getTaskId().toString() : null);
                setMDCIfNotNull("batchId", entity.getBatchId() != null ? entity.getBatchId().toString() : null);
            } else if (arg instanceof SubTaskEntity) {
                SubTaskEntity entity = (SubTaskEntity) arg;
                setMDCIfNotNull("subTaskId", entity.getSubTaskId() != null ? entity.getSubTaskId().toString() : null);
                setMDCIfNotNull("taskId", entity.getTaskId() != null ? entity.getTaskId().toString() : null);
            }
            // Extract from UUID arguments (common pattern)
            else if (arg instanceof UUID) {
                // Try to determine what type of ID this might be based on method context
                String argStr = arg.toString();
                if (!hasMDCKey("batchId")) {
                    setMDCIfNotNull("batchId", argStr);
                } else if (!hasMDCKey("taskId")) {
                    setMDCIfNotNull("taskId", argStr);
                } else if (!hasMDCKey("subTaskId")) {
                    setMDCIfNotNull("subTaskId", argStr);
                }
            }
            // Extract from String arguments that look like UUIDs
            else if (arg instanceof String) {
                String str = (String) arg;
                if (isUUID(str)) {
                    if (!hasMDCKey("taskId")) {
                        setMDCIfNotNull("taskId", str);
                    }
                }
            }
        }
    }

    private void extractAndSetMDCFromReturnValue(Object result) {
        if (result == null) return;
        
        if (result instanceof BatchResponse) {
            BatchResponse resp = (BatchResponse) result;
            setMDCIfNotNull("batchId", resp.getBatchId());
        } else if (result instanceof TaskResponse) {
            TaskResponse resp = (TaskResponse) result;
            setMDCIfNotNull("taskId", resp.getTaskId());
        }
    }

    private void setMDCIfNotNull(String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            MDC.put(key, value);
        }
    }

    private boolean isUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean hasMDCKey(String key) {
        var contextMap = MDC.getCopyOfContextMap();
        return contextMap != null && contextMap.containsKey(key);
    }
}