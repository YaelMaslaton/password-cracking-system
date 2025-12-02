package com.example.minion.util;

import org.slf4j.MDC;

public final class LogUtils {
    
    private static final String TASK_ID_KEY = "taskId";

    private static final String MINION_ID_KEY = "minionId";
    
    private LogUtils() {
        // Prevent instantiation
    }
    
    public static void setTaskId(String taskId) {
        if (taskId != null && !taskId.trim().isEmpty()) {
            MDC.put(TASK_ID_KEY, taskId);
        }
    }
    

    
    public static void setMinionId(String minionId) {
        if (minionId != null && !minionId.trim().isEmpty()) {
            MDC.put(MINION_ID_KEY, minionId);
        }
    }
    
    public static void clearMDC() {
        MDC.clear();
    }
}