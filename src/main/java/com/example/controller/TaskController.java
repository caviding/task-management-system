package com.example.controller;

import jakarta.validation.Valid;
import com.example.Enum.TaskStatus;
import com.example.entity.RootEntity;
import com.example.service.ITaskService;
import com.example.dto.response.TaskResponseDto;
import com.example.dto.request.TaskRequestDto;
import org.springframework.data.domain.*;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import com.example.dto.request.TaskUpdateDto;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("api/v1/tasks")
@Validated
public class TaskController {

    private final ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public RootEntity<TaskResponseDto> createTask(@RequestBody @Valid TaskRequestDto taskRequestDto) {
        return RootEntity.success(taskService.createTask(taskRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public RootEntity<TaskResponseDto> createTaskById(@PathVariable @Positive(message = "Id cannot be negative !!") Long userId,
                                                    @RequestBody @Valid TaskRequestDto taskRequestDto) {
        return RootEntity.success(taskService.createTaskById(userId, taskRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, authentication.name)")
    public RootEntity<TaskResponseDto> getTaskById(@PathVariable @Positive(message = "Id cannot be negative !!") Long id) {
        return RootEntity.success(taskService.getTaskById(id),HttpStatus.OK);
    }

    @DeleteMapping({"/{id}"})
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, authentication.name)")
    public RootEntity<Void> deleteTask(@PathVariable(name = "id") @Positive(message = "TaskId cannot be negative !!") Long id) {
        taskService.deleteTask(id);
        return RootEntity.success(null,HttpStatus.NO_CONTENT);
    }

    @PatchMapping({"/{id}/status","/{id}/status"})
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, authentication.name)")
    public RootEntity<TaskResponseDto> updateTaskStatus(@PathVariable @Positive(message = "Id cannot be negative !!") Long id,
                                                        @RequestParam(name = "status") TaskStatus status) {
        return RootEntity.success(taskService.updateTaskStatus(id,status),HttpStatus.OK);
    }

    @PutMapping({"/{id}","/{id}/"})
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isOwner(#id, authentication.name)")
    public RootEntity<TaskResponseDto> updateTask(@PathVariable @Positive(message = "Id cannot be negative !!") Long id,
                                                  @RequestBody @Valid TaskUpdateDto taskUpdateDto) {
        return RootEntity.success(taskService.updateTask(id,taskUpdateDto),HttpStatus.OK);
    }

    @GetMapping("/my-tasks")
    public RootEntity<Page<TaskResponseDto>> getMyTasks(@RequestParam(name = "pageNumber",defaultValue = "0") @Min(value = 0,message = "Page number cannot be negative !!") int pageNumber,
                                                        @RequestParam(name = "pageSize",defaultValue = "5") @Min(value = 1,message = "Page size cannot be lower than 1 !!") int pageSize) {
        Sort.Order order = Sort.Order.asc("id");
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by(order));
        Page<TaskResponseDto> page = taskService.getMyTasks(pageable);
        return RootEntity.success(page,HttpStatus.OK);
    }

    @GetMapping({"/list","/list/"})
    @PreAuthorize("hasRole('ADMIN')")
    public RootEntity<Page<TaskResponseDto>> getAllTasks(@RequestParam(name = "status",required = false) TaskStatus status,
                                                         @RequestParam(name = "pageNumber",defaultValue = "0") @Min(value = 0,message = "Page number cannot be negative !!") int pageNumber,
                                                         @RequestParam(name = "pageSize",defaultValue = "5") @Min(value = 1,message = "Page size cannot be lower than 1 !!") int pageSize,
                                                         @RequestParam(name = "sort",defaultValue = "id") String sort) {
        Sort.Order order = Sort.Order.asc(sort);
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by(order));
        Page<TaskResponseDto> page = taskService.getAllTasks(status,pageable);
        return RootEntity.success(page,HttpStatus.OK);
    }

    @GetMapping({"/user-tasks/{user_id}/","/user-tasks/{user_id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public RootEntity<Page<TaskResponseDto>> getTasksByUser(@PathVariable(name = "user_id") @Positive(message = "Id cannot be negative !!") Long user_id,
                                                            @RequestParam(name = "status", required = false) TaskStatus status,
                                                            @RequestParam(name = "pageNumber",defaultValue = "0") @Min(value = 0,message = "Page number cannot be negative !!") int pageNumber,
                                                            @RequestParam(name = "pageSize",defaultValue = "5") @Min(value = 1,message = "Page size cannot be lower than 1 !!") int pageSize,
                                                            @RequestParam(name = "sort",defaultValue = "id") String sort) {
        Sort.Order order = Sort.Order.asc(sort);
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by(order));
        Page<TaskResponseDto> page = taskService.getTasksByUser(user_id,status,pageable);
        return RootEntity.success(page,HttpStatus.OK);
    }

    @GetMapping({"/search","/search/"})
    @PreAuthorize("hasRole('ADMIN')")
    public RootEntity<Page<TaskResponseDto>> getTasks(@RequestParam(name = "user_id",required = false) @Positive(message = "Id cannot be negative !!") Long user_id,
                                                      @RequestParam(name = "status",required = false) TaskStatus status,
                                                      @RequestParam(name = "title",required = false) String title,
                                                      @RequestParam(name = "pageNumber",defaultValue = "0") @Min(value = 0,message = "Page number cannot be negative !!") int pageNumber,
                                                      @RequestParam(name = "pageSize",defaultValue = "5") @Min(value = 1,message = "Page size cannot be lower than 1 !!") int pageSize,
                                                      @RequestParam(name = "sort",required = false,defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by(sort));
        Page<TaskResponseDto> page = taskService.getTasks(user_id,status,title,pageable);
        return RootEntity.success(page,HttpStatus.OK);
    }
}
