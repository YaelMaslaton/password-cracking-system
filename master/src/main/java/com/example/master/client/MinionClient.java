package com.example.master.client;

import com.example.master.config.MinionProperties;
import com.example.master.dto.MinionRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class MinionClient {

    private final MinionProperties minionProperties;
    private final RestTemplate restTemplate;

    public MinionClient(MinionProperties minionProperties, RestTemplate restTemplate) {
        this.minionProperties = minionProperties;
        this.restTemplate = restTemplate;
    }

    public void sendCrackRequest(int minionIndex, MinionRequest request) {
        if (minionIndex < 0 || minionIndex >= getMinionCount()) {
            throw new IllegalArgumentException("Invalid minion index: " + minionIndex);
        }

        String minionUrl = getMinionUrlByIndex(minionIndex);
        
        // Set MDC context for this request
        MDC.put("subTaskId", request.getTaskId());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<MinionRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            log.debug("Sending crack request to minion {} at {}", minionIndex, minionUrl);
            restTemplate.postForEntity(minionUrl, entity, String.class);
            log.debug("Successfully sent crack request to minion {}", minionIndex);
        } catch (Exception e) {
            log.error("Failed to send request to minion {} at {}: {}", minionIndex, minionUrl, e.getMessage(), e);
            throw e;
        }
    }

    public int getMinionCount() {
        return minionProperties.getServers() != null ? minionProperties.getServers().size() : 0;
    }

    public String getMinionUrlByIndex(int index) {
        if (minionProperties.getServers() == null || index < 0 || index >= minionProperties.getServers().size()) {
            throw new IllegalArgumentException("Invalid minion index: " + index);
        }
        return minionProperties.getServers().get(index).getBaseUrl() + "/crack";
    }


}