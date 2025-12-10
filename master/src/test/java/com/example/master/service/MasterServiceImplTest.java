package com.example.master.service;

import com.example.master.dto.BatchResponse;
import com.example.master.dto.ResultRequest;
import com.example.master.dto.TaskResponse;
import com.example.master.entity.BatchEntity;
import com.example.master.entity.TaskEntity;
import com.example.master.enums.BatchStatus;
import com.example.master.enums.TaskStatus;
import com.example.master.repository.BatchRepository;
import com.example.master.repository.TaskRepository;
import com.example.master.validator.HashFileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MasterServiceImplTest {

    @Mock
    private BatchRepository batchRepository;
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private HashFileValidator hashFileValidator;
    
    @Mock
    private TaskOrchestrator taskOrchestrator;
    
    @Mock
    private ResultProcessor resultProcessor;
    
    @Mock
    private ExecutorService minionDispatchExecutor;
    
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private MasterServiceImpl masterService;

    private UUID testTaskId;
    private TaskEntity testTask;

    @BeforeEach
    void setUp() {
        testTaskId = UUID.randomUUID();
        testTask = new TaskEntity(testTaskId, UUID.randomUUID(), "test-hash", TaskStatus.RUNNING, null, 3, 0);
    }

    @Test
    void submitHashes_ShouldReturnBatchResponse() throws IOException {
        // Given
        List<String> hashes = List.of("hash1", "hash2");
        when(hashFileValidator.validateAndExtractHashes(multipartFile)).thenReturn(hashes);
        when(taskOrchestrator.createAndDispatchTask(any(), any())).thenReturn("task-id-1", "task-id-2");

        // When
        BatchResponse response = masterService.submitHashes(multipartFile);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getTasksCount());
        assertEquals(BatchStatus.SUBMITTED, response.getStatus());
        verify(batchRepository).save(any(BatchEntity.class));
        verify(taskOrchestrator, times(2)).createAndDispatchTask(any(), any());
    }

    @Test
    void processResult_ShouldDelegateToResultProcessor() {
        // Given
        ResultRequest request = new ResultRequest();
        request.setTaskId("task-id");
        request.setStatus(TaskStatus.FOUND);
        request.setPassword("password123");

        // When
        masterService.processResult(request);

        // Then
        verify(resultProcessor).processResult(request);
    }

    @Test
    void getTaskStatus_WithValidId_ShouldReturnTaskResponse() {
        // Given
        when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));

        // When
        TaskResponse response = masterService.getTaskStatus(testTaskId.toString());

        // Then
        assertNotNull(response);
        assertEquals(testTaskId.toString(), response.getTaskId());
        assertEquals("test-hash", response.getHash());
        assertEquals(TaskStatus.RUNNING, response.getStatus());
    }

    @Test
    void getTaskStatus_WithNonExistentId_ShouldReturnNull() {
        // Given
        when(taskRepository.findById(testTaskId)).thenReturn(Optional.empty());

        // When
        TaskResponse response = masterService.getTaskStatus(testTaskId.toString());

        // Then
        assertNull(response);
    }

    @Test
    void getTaskStatus_WithNullId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> masterService.getTaskStatus(null)
        );
        assertEquals("TaskId cannot be null or empty", exception.getMessage());
    }

    @Test
    void getTaskStatus_WithEmptyId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> masterService.getTaskStatus("  ")
        );
        assertEquals("TaskId cannot be null or empty", exception.getMessage());
    }

    @Test
    void getTaskStatus_WithInvalidUUID_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> masterService.getTaskStatus("invalid-uuid")
        );
        assertTrue(exception.getMessage().contains("Invalid taskId format"));
    }
}