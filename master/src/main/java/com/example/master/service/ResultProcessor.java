package com.example.master.service;

import com.example.master.dto.ResultRequest;
import com.example.master.entity.SubTaskEntity;
import com.example.master.entity.TaskEntity;
import com.example.master.enums.SubTaskStatus;
import com.example.master.enums.TaskStatus;
import com.example.master.repository.SubTaskRepository;
import com.example.master.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.example.master.common.AppConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultProcessor {

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final BatchManager batchManager;

    @Transactional
    public void processResult(ResultRequest resultRequest) {
        UUID subTaskId = UUID.fromString(resultRequest.getTaskId());
        
        SubTaskEntity subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_SUBTASK_NOT_FOUND + subTaskId));

        TaskEntity task = taskRepository.findById(subTask.getTaskId()).orElseThrow();

        if (task.getStatus() == TaskStatus.FOUND || task.getStatus() == TaskStatus.NOT_FOUND) {
            return;
        }

        if (resultRequest.getStatus() == TaskStatus.FOUND) {
            handleFoundResult(subTask, task, resultRequest.getPassword());
        } else {
            handleNotFoundResult(subTask, task);
        }
    }

    private void handleFoundResult(SubTaskEntity subTask, TaskEntity task, String password) {
        subTask.setStatus(SubTaskStatus.COMPLETED);
        subTask.setResultPassword(password);
        subTaskRepository.save(subTask);

        task.setStatus(TaskStatus.FOUND);
        task.setFoundPassword(password);
        task.setCompletedSubTasks(task.getTotalSubTasks());
        taskRepository.save(task);

        cancelRemainingSubTasks(task.getTaskId());
        batchManager.finalizeTask(task);
    }

    private void handleNotFoundResult(SubTaskEntity subTask, TaskEntity task) {
        subTask.setStatus(SubTaskStatus.COMPLETED);
        subTaskRepository.save(subTask);

        int completed = task.getCompletedSubTasks() + 1;
        task.setCompletedSubTasks(completed);

        if (completed >= task.getTotalSubTasks()) {
            task.setStatus(TaskStatus.NOT_FOUND);
            batchManager.finalizeTask(task);
        }
        taskRepository.save(task);
    }

    private void cancelRemainingSubTasks(UUID taskId) {
        List<SubTaskEntity> runningSubs = subTaskRepository.findByTaskIdAndStatus(taskId, SubTaskStatus.RUNNING);
        for (SubTaskEntity other : runningSubs) {
            other.setStatus(SubTaskStatus.CANCELLED);
            subTaskRepository.save(other);
        }
    }
}