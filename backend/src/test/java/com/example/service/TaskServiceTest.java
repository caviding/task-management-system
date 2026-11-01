package com.example.service;

import com.example.Enum.Role;
import com.example.Enum.TaskStatus;
import com.example.dto.request.TaskRequestDto;
import com.example.dto.request.TaskUpdateDto;
import com.example.dto.response.TaskResponseDto;
import com.example.entity.Task;
import com.example.entity.User;
import com.example.exception.TaskNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import com.example.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskServiceImpl sut;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    private Task testTask;
    private User testUser;
    private TaskRequestDto taskRequestDto;
    private TaskUpdateDto taskUpdateDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@gmail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        testTask = Task.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .status(TaskStatus.TODO)
                .userId(1L)
                .build();

        taskRequestDto = TaskRequestDto.builder()
                .title("test title")
                .description("test description")
                .status(TaskStatus.TODO)
                .build();

        taskUpdateDto = TaskUpdateDto.builder()
                .title("updated title")
                .description("updated description")
                .status(TaskStatus.DONE)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("createTask should create and return Task Response Dto")
    void should_createTask_returnTaskResponseDto() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("testUser"))
                .thenReturn(Optional.of(testUser));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(testTask);

        TaskResponseDto result = sut.createTask(taskRequestDto);

        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        verify(userRepository, times(1))
                .findByUsername("testUser");

        verify(taskRepository, times(1))
                .save(any(Task.class));
    }

    @Test
    @DisplayName("createTask should throw UserNotFoundException when user not found")
    void should_createTask_throwUserNotFoundExceptionWhenUserNotFound() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "unknownUser",
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("unknownUser"))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.createTask(taskRequestDto)
        );

        assertEquals("User not found !!", exception.getMessage());

        verify(userRepository, times(1))
                .findByUsername("unknownUser");

        verify(taskRepository, never())
                .save(any(Task.class));
    }

    @Test
    @DisplayName("createTaskById should return Task response dto")
    void should_createTaskById_returnTaskResponseDto() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(testTask);

        TaskResponseDto result =
                sut.createTaskById(userId, taskRequestDto);

        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        verify(userRepository, times(1))
                .findById(userId);

        verify(taskRepository, times(1))
                .save(any(Task.class));

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    @DisplayName("createTaskById should throw UserNotFoundException when user not found")
    void should_createTaskById_throwUserNotFoundException_whenUserNotFound() {
        Long userId = 99L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.createTaskById(userId, taskRequestDto)
        );

        assertEquals("User not found !!", exception.getMessage());

        verify(userRepository, times(1))
                .findById(userId);

        verify(taskRepository, never())
                .save(any(Task.class));
    }

    @Test
    @DisplayName("getTaskById should return Task response dto")
    void should_getTaskById_returnTaskResponseDto() {
        Long id = 1L;

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(testTask));

        TaskResponseDto result = sut.getTaskById(id);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        verify(taskRepository, times(1))
                .findById(id);
    }

    @Test
    @DisplayName("getTaskById should throw TaskNotFoundException when task not found")
    void should_getTaskById_throwTaskNotFoundException_whenTaskNotFound() {
        Long id = 99L;

        when(taskRepository.findById(id))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.getTaskById(id)
        );

        assertEquals("Task not found !!", exception.getMessage());

        verify(taskRepository, times(1))
                .findById(id);
    }

    @Test
    @DisplayName("deleteTask should delete task")
    void should_deleteTask() {
        Long id = 1L;

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(testTask));

        doNothing()
                .when(taskRepository)
                .delete(testTask);

        sut.deleteTask(id);

        verify(taskRepository, times(1))
                .findById(id);

        verify(taskRepository, times(1))
                .delete(testTask);

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("deleteTask should throw TaskNotFoundException when task not found")
    void should_deleteTask_throwTaskNotFoundException_whenTaskNotFound() {
        Long id = 99L;

        when(taskRepository.findById(id))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.deleteTask(id)
        );

        assertEquals("Task not found !!", exception.getMessage());

        verify(taskRepository, times(1))
                .findById(id);

        verify(taskRepository, never())
                .delete(any(Task.class));

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("isOwner should return true")
    void should_isOwner_returnTrue() {
        Long taskId = 1L;

        when(userRepository.findByUsername("testUser"))
                .thenReturn(Optional.of(testUser));

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(testTask));

        boolean result = sut.isOwner(taskId, "testUser");

        assertTrue(result);

        verify(userRepository, times(1))
                .findByUsername("testUser");

        verify(taskRepository, times(1))
                .findById(taskId);
    }

    @Test
    @DisplayName("isOwner should return false")
    void should_isOwner_returnFalse() {
        Long taskId = 1L;

        User anotherUser = User.builder()
                .id(2L)
                .username("anotherUser")
                .email("another@gmail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("anotherUser"))
                .thenReturn(Optional.of(anotherUser));

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(testTask));

        boolean result =
                sut.isOwner(taskId, "anotherUser");

        assertFalse(result);

        verify(userRepository, times(1))
                .findByUsername("anotherUser");

        verify(taskRepository, times(1))
                .findById(taskId);
    }

    @Test
    @DisplayName("isOwner should throw UserNotFoundException")
    void should_isOwner_throwUserNotFoundException_whenUserNotFound() {
        Long taskId = 1L;

        when(userRepository.findByUsername("unknownUser"))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.isOwner(taskId, "unknownUser")
        );

        assertEquals("User not found !!", exception.getMessage());

        verify(userRepository, times(1))
                .findByUsername("unknownUser");

        verify(taskRepository, never())
                .findById(anyLong());
    }

    @Test
    @DisplayName("isOwner should throw TaskNotFoundException")
    void should_isOwner_throwTaskNotFoundException_whenTaskNotFound() {
        Long taskId = 99L;

        when(userRepository.findByUsername("testUser"))
                .thenReturn(Optional.of(testUser));

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.isOwner(taskId, "testUser")
        );

        assertEquals("Task not found !!", exception.getMessage());

        verify(userRepository, times(1))
                .findByUsername("testUser");

        verify(taskRepository, times(1))
                .findById(taskId);
    }

    @Test
    @DisplayName("updateTaskStatus should return Task response dto")
    void should_updateTaskStatus() {
        Long id = 1L;

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(testTask));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(testTask);

        TaskResponseDto result =
                sut.updateTaskStatus(id, TaskStatus.DONE);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(TaskStatus.DONE, result.getStatus());

        verify(taskRepository, times(1))
                .findById(id);

        verify(taskRepository, times(1))
                .save(testTask);
    }

    @Test
    @DisplayName("updateTaskStatus should throw TaskNotFoundException")
    void should_updateTaskStatus_throwTaskNotFoundException_whenTaskNotFound() {
        Long id = 99L;

        when(taskRepository.findById(id))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.updateTaskStatus(id, TaskStatus.DONE)
        );

        assertEquals("Task not found !!", exception.getMessage());

        verify(taskRepository, times(1))
                .findById(id);

        verify(taskRepository, never())
                .save(any(Task.class));
    }

    @Test
    @DisplayName("updateTask should return Task response dto")
    void should_updateTask() {
        Long id = 1L;

        when(taskRepository.findById(id))
                .thenReturn(Optional.of(testTask));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(testTask);

        TaskResponseDto result =
                sut.updateTask(id, taskUpdateDto);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals("updated title", result.getTitle());
        assertEquals("updated description", result.getDescription());
        assertEquals(TaskStatus.DONE, result.getStatus());

        verify(taskRepository, times(1))
                .findById(id);

        verify(taskRepository, times(1))
                .save(testTask);
    }

    @Test
    @DisplayName("updateTask should throw TaskNotFoundException")
    void should_updateTask_throwTaskNotFoundException_whenTaskNotFound() {
        Long id = 99L;

        when(taskRepository.findById(id))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.updateTask(id, taskUpdateDto)
        );

        assertEquals("Task not found !!", exception.getMessage());

        verify(taskRepository, times(1))
                .findById(id);

        verify(taskRepository, never())
                .save(any(Task.class));
    }

    @Test
    @DisplayName("getMyTasks should return Task response dto page")
    void should_getMyTasks_returnTaskPage() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("testUser"))
                .thenReturn(Optional.of(testUser));

        when(taskRepository.findByUserId(
                eq(1L),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(List.of(testTask))
        );

        Page<TaskResponseDto> result =
                sut.getMyTasks(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTask.getId(), result.getContent().get(0).getId());

        verify(userRepository, times(1))
                .findByUsername("testUser");

        verify(taskRepository, times(1))
                .findByUserId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("getMyTasks should throw UserNotFoundException")
    void should_getMyTasks_throwUserNotFoundException_whenUserNotFound() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("testUser"))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.getMyTasks(Pageable.unpaged())
        );

        assertEquals("User not found !!", exception.getMessage());

        verify(userRepository, times(1))
                .findByUsername("testUser");

        verify(taskRepository, never())
                .findByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("getAllTasks should return all tasks when status is null")
    void should_getAllTasks_returnTaskPageWhenStatusIsNull() {
        when(taskRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testTask)));

        Page<TaskResponseDto> result =
                sut.getAllTasks(null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTask.getId(), result.getContent().get(0).getId());

        verify(taskRepository, times(1))
                .findAll(any(Pageable.class));

        verify(taskRepository, never())
                .findByStatus(any(TaskStatus.class), any(Pageable.class));
    }

    @Test
    @DisplayName("getAllTasks should return tasks filtered by status")
    void should_getAllTasks_returnTaskPageWhenStatusIsProvided() {
        when(taskRepository.findByStatus(
                eq(TaskStatus.DONE),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(List.of(testTask))
        );

        Page<TaskResponseDto> result =
                sut.getAllTasks(TaskStatus.DONE, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(taskRepository, times(1))
                .findByStatus(eq(TaskStatus.DONE), any(Pageable.class));

        verify(taskRepository, never())
                .findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("getTasksByUser should return user tasks when status is null")
    void should_getTasksByUser_returnTaskPageWhenStatusIsNull() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));

        when(taskRepository.findByUserId(
                eq(1L),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(List.of(testTask))
        );

        Page<TaskResponseDto> result =
                sut.getTasksByUser(1L, null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(userRepository, times(1))
                .findById(1L);

        verify(taskRepository, times(1))
                .findByUserId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("getTasksByUser should return user tasks filtered by status")
    void should_getTasksByUser_returnTaskPageWhenStatusIsProvided() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));

        when(taskRepository.findByUserIdAndStatus(
                eq(1L),
                eq(TaskStatus.DONE),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(List.of(testTask))
        );

        Page<TaskResponseDto> result =
                sut.getTasksByUser(
                        1L,
                        TaskStatus.DONE,
                        Pageable.unpaged()
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(userRepository, times(1))
                .findById(1L);

        verify(taskRepository, times(1))
                .findByUserIdAndStatus(
                        eq(1L),
                        eq(TaskStatus.DONE),
                        any(Pageable.class)
                );
    }

    @Test
    @DisplayName("getTasksByUser should throw UserNotFoundException")
    void should_getTasksByUser_throwUserNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.getTasksByUser(
                        1L,
                        null,
                        Pageable.unpaged()
                )
        );

        assertEquals("User not found !!", exception.getMessage());

        verify(userRepository, times(1))
                .findById(1L);

        verify(taskRepository, never())
                .findByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("getTasks should return tasks when user id is provided")
    void should_getTasks_returnTaskPageWhenUserIdIsProvided() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));

        when(taskRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(List.of(testTask))
        );

        Page<TaskResponseDto> result =
                sut.getTasks(
                        1L,
                        TaskStatus.TODO,
                        "sss",
                        Pageable.unpaged()
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(userRepository, times(1))
                .findById(1L);

        verify(taskRepository, times(1))
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }

    @Test
    @DisplayName("getTasks should throw UserNotFoundException when user not found")
    void getTasks_should_throwUserNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.getTasks(
                        1L,
                        TaskStatus.TODO,
                        "sss",
                        Pageable.unpaged()
                )
        );

        assertEquals("User not found !!", exception.getMessage());

        verify(userRepository, times(1))
                .findById(1L);

        verify(taskRepository, never())
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }

    @Test
    @DisplayName("getTasks should return tasks when there is no filter")
    void getTasks_should_returnTaskPageWhenSpecificationIsNull() {

        when(taskRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(List.of(testTask))
        );

        Page<TaskResponseDto> result =
                sut.getTasks(
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(
                testTask.getId(),
                result.getContent().get(0).getId()
        );

        verify(taskRepository, times(1))
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );

        verify(userRepository, never())
                .findById(anyLong());
    }
}