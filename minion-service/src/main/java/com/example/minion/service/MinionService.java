package com.example.minion.service;

import com.example.minion.constants.MinionConstants;
import com.example.minion.dto.CrackRequest;
import com.example.minion.dto.ResultRequest;
import com.example.minion.enums.TaskStatus;
import com.example.minion.util.LogUtils;
import com.example.minion.util.MD5Util;
import com.example.minion.util.PhoneFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinionService implements CrackingService {
    
    private final MasterClient masterClient;
    
    @Value("${minion.id}")
    private String minionId;
    
    @Override
    public void crack(CrackRequest request) {

        if (!request.isValidRange()) {
            log.warn("Invalid range: rangeFrom={}, rangeTo={}", 
                    request.getRangeFrom(), request.getRangeTo());
            return;
        }
        
        log.info("Starting crack task: hash={}, range={}-{}", 
                request.getHash(), request.getRangeFrom(), request.getRangeTo());
        
        boolean passwordFound = performBruteForceAttack(request);
        
        if (!passwordFound) {
            log.info("Password not found in range: {}-{}", 
                    request.getRangeFrom(), request.getRangeTo());
            sendResult(request.getTaskId(), TaskStatus.NOT_FOUND, null);
        }
        
        log.info("Crack task completed");
    }
    
    private boolean performBruteForceAttack(CrackRequest request) {
        long totalNumbers = request.getRangeTo() - request.getRangeFrom() + 1;
        log.debug("Processing {} phone numbers in range", totalNumbers);
        
        for (long number = request.getRangeFrom(); number <= request.getRangeTo(); number++) {
            if (testPhoneNumber(request, number)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean testPhoneNumber(CrackRequest request, long number) {
        String phone = PhoneFormatter.formatPhone(number);
        String hash = MD5Util.computeHash(phone);
        
        if (number <= request.getRangeFrom() + MinionConstants.DEBUG_LOG_LIMIT) {
            log.debug("Testing: {} -> {}", phone, hash);
        }
        
        if (hash.equals(request.getHash())) {
            handlePasswordFound(request, number, phone);
            return true;
        }
        
        return false;
    }
    
    private void handlePasswordFound(CrackRequest request, long number, String phone) {
        log.info("Password FOUND: password={}", phone);
        sendResult(request.getTaskId(), TaskStatus.FOUND, phone);
    }
    
    private void sendResult(String taskId, TaskStatus status, String password) {
        ResultRequest result = ResultRequest.builder()
                .taskId(taskId)
                .status(status)
                .password(password)
                .build();
        
        masterClient.sendResult(result);
    }
}