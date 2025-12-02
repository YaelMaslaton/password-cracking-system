package com.example.minion.service;

import com.example.minion.dto.CrackRequest;
import com.example.minion.dto.ResultRequest;
import com.example.minion.util.MD5Util;
import com.example.minion.util.PhoneFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinionServiceTest {

    @Mock
    private MasterClient masterClient;

    private CrackingService crackingService;

    @BeforeEach
    void setUp() {
        crackingService = new MinionService(masterClient);
        ReflectionTestUtils.setField(crackingService, "minionId", "test-minion");
    }

    @Test
    void crack_shouldFindPasswordAndSendFoundResult() {
        // Given - Use a simple test case
        String testPhone = PhoneFormatter.formatPhone(500000000L);
        String testHash = MD5Util.computeHash(testPhone);
        
        CrackRequest request = CrackRequest.builder()
                .taskId("task-123")
                .hash(testHash)  // Use actual computed hash
                .rangeFrom(500000000L)
                .rangeTo(500000000L)
                .build();

        // When
        crackingService.crack(request);

        // Then
        ArgumentCaptor<ResultRequest> captor = ArgumentCaptor.forClass(ResultRequest.class);
        verify(masterClient, times(1)).sendResult(captor.capture());

        ResultRequest sentResult = captor.getValue();
        assertEquals("task-123", sentResult.getTaskId());
        assertEquals("FOUND", sentResult.getStatus());
        assertEquals("055-00000000", sentResult.getPassword());
    }

    @Test
    void crack_shouldSendNotFoundWhenPasswordNotInRange() {
        // Given
        CrackRequest request = CrackRequest.builder()
                .taskId("task-456")
                .hash("nonexistenthash123456789")
                .rangeFrom(500000000L)
                .rangeTo(500000002L)
                .build();

        // When
        crackingService.crack(request);

        // Then
        ArgumentCaptor<ResultRequest> captor = ArgumentCaptor.forClass(ResultRequest.class);
        verify(masterClient, times(1)).sendResult(captor.capture());

        ResultRequest sentResult = captor.getValue();
        assertEquals("task-456", sentResult.getTaskId());
        assertEquals("NOT_FOUND", sentResult.getStatus());
        assertEquals(null, sentResult.getPassword());
    }

    @Test
    void crack_shouldHandleInvalidRange() {
        // Given
        CrackRequest request = CrackRequest.builder()
                .taskId("task-789")
                .hash("somehash")
                .rangeFrom(500000001L)
                .rangeTo(500000000L) // Invalid: from > to
                .build();

        // When
        crackingService.crack(request);

        // Then
        verify(masterClient, never()).sendResult(any());
    }

    @Test
    void crack_shouldHandleNullRange() {
        // Given
        CrackRequest request = CrackRequest.builder()
                .taskId("task-null")
                .hash("somehash")
                .rangeFrom(null)
                .rangeTo(500000000L)
                .build();

        // When
        crackingService.crack(request);

        // Then
        verify(masterClient, never()).sendResult(any());
    }

    @Test
    void crack_shouldStopOnFirstMatch() {
        // Given - Use actual computed hash
        String testPhone = PhoneFormatter.formatPhone(500000000L);
        String testHash = MD5Util.computeHash(testPhone);
        
        CrackRequest request = CrackRequest.builder()
                .taskId("task-first")
                .hash(testHash)  // Use actual computed hash
                .rangeFrom(500000000L)
                .rangeTo(500000010L) // Larger range to test early exit
                .build();

        // When
        crackingService.crack(request);

        // Then
        ArgumentCaptor<ResultRequest> captor = ArgumentCaptor.forClass(ResultRequest.class);
        verify(masterClient, times(1)).sendResult(captor.capture());

        ResultRequest sentResult = captor.getValue();
        assertEquals("FOUND", sentResult.getStatus());
        assertEquals("055-00000000", sentResult.getPassword());
    }
}