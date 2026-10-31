package com.example.mapper;

import com.example.dto.response.TaskResponseDto;
import com.example.dto.request.TaskRequestDto;
import com.example.entity.Task;

public class TaskMapper {

    public static TaskResponseDto toDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }

    public static Task toEntity(TaskRequestDto taskRequestDto) {
        Task task = new Task();
        task.setTitle(taskRequestDto.getTitle());
        task.setDescription(taskRequestDto.getDescription());
        task.setStatus(taskRequestDto.getStatus());
        return task;
    }
}
