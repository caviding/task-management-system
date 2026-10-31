package com.example.service.impl;

import com.example.entity.User;
import com.example.entity.Task;
import com.example.Enum.TaskStatus;
import com.example.mapper.TaskMapper;
import com.example.service.ITaskService;
import com.example.dto.response.TaskResponseDto;
import com.example.dto.request.TaskRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.example.dto.request.TaskUpdateDto;
import com.example.repository.UserRepository;
import com.example.repository.TaskRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import com.example.exception.UserNotFoundException;
import com.example.exception.TaskNotFoundException;
import com.example.specifications.TaskSpecification;
import org.springframework.data.jpa.domain.Specification;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements ITaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
            Task task = TaskMapper.toEntity(taskRequestDto);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found !!"));

            task.setUserId(user.getId());
            user.getTasks().add(task);
            userRepository.save(user);

            return TaskMapper.toDto(task);
    }

    @Override
    public TaskResponseDto createTaskById(Long userId, TaskRequestDto taskRequestDto) {
        Task task = TaskMapper.toEntity(taskRequestDto);
        task.setUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found !!"));
        user.getTasks().add(task);
        userRepository.save(user);
        return TaskMapper.toDto(task);
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        return TaskMapper.toDto(taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task not found !!")));
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found !!"));

        User user =userRepository.findById(task.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found !!"));

        user.getTasks().remove(task);
        userRepository.save(user);
    }

    @Override
    public boolean isOwner(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found !!"));

        Task task = taskRepository.findById(id)
                        .orElseThrow(() -> new TaskNotFoundException("Task not found !!"));

        return user.getTasks().contains(task);
    }

    @Override
    public TaskResponseDto updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found !!"));
        task.setStatus(status);
        taskRepository.save(task);
        return TaskMapper.toDto(task);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskUpdateDto taskUpdateDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found !!"));
        task.setTitle(taskUpdateDto.getTitle());
        task.setDescription(taskUpdateDto.getDescription());
        task.setStatus(taskUpdateDto.getStatus());
        taskRepository.save(task);
        return TaskMapper.toDto(task);
    }

    @Override
    public Page<TaskResponseDto> getMyTasks(Pageable pageable) {

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UserNotFoundException("User not found !!"));
        return taskRepository.findByUserId(user.getId(),pageable).map(TaskMapper::toDto);
    }

    @Override
    public Page<TaskResponseDto> getAllTasks(TaskStatus status, Pageable pageable) {
        if (status == null){
            return taskRepository.findAll(pageable).map(TaskMapper::toDto);
        }
        return taskRepository.findByStatus(status,pageable).map(TaskMapper::toDto);
    }

    @Override
    public Page<TaskResponseDto> getTasksByUser(Long user_id, TaskStatus status, Pageable pageable) {
        userRepository.findById(user_id)
                .orElseThrow(() -> new UserNotFoundException("User not found !!"));
        if (status == null){
            return taskRepository.findByUserId(user_id,pageable).map(TaskMapper::toDto);
        }
        return taskRepository.findByUserIdAndStatus(user_id,status,pageable).map(TaskMapper::toDto);
    }

    @Override
    public Page<TaskResponseDto> getTasks(Long user_id, TaskStatus status, String title, Pageable pageable) {

        if (user_id != null){
            userRepository.findById(user_id)
                    .orElseThrow(() -> new UserNotFoundException("User not found !!"));
        }
        Specification<Task> spec = (root, query, cb) -> cb.conjunction();

        if (user_id != null){
            spec = spec.and(TaskSpecification.hasUserId(user_id));
        }
        if (status != null){
            spec = spec.and(TaskSpecification.hasStatus(status));
        }
        if (title != null){
            spec = spec.and(TaskSpecification.hasTitle(title));
        }
        return taskRepository.findAll(spec,pageable).map(TaskMapper::toDto);
    }
}
