package com.example.service;

import org.mockito.Mock;
import com.example.Enum.Role;
import com.example.entity.Task;
import com.example.entity.User;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import com.example.Enum.TaskStatus;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.data.domain.Page;
import com.example.repository.TaskRepository;
import com.example.dto.request.TaskUpdateDto;
import com.example.repository.UserRepository;
import com.example.dto.request.TaskRequestDto;
import com.example.dto.response.TaskResponseDto;
import com.example.service.impl.TaskServiceImpl;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.exception.TaskNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.exception.TaskUserMismatchException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .tasks(new ArrayList<>())
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
                new UsernamePasswordAuthenticationToken("testUser", null , List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        when(taskRepository.findByTitleAndUserId(anyString(), any(Long.class))).thenReturn(Optional.of(testTask));

        TaskResponseDto result = sut.createTask(taskRequestDto);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(taskRepository, times(1)).findByTitleAndUserId(anyString(), any(Long.class));
    }

    @Test
    @DisplayName("createTask should throw UserNotFoundException when user not found ")
    void should_createTask_throwUserNotFoundExceptionWhenUserNotFound() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("unknownUser", null , List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.createTask(taskRequestDto),
                "UserNotFoundException should be thrown"
        );

        assertEquals("User not found !!", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    @DisplayName("createtask should throw TaskNotFoundEXception when task not found")
    void should_createTask_throwTaskNotFoundExceptionWhenTaskNotFound() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testUser", null , List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        when(taskRepository.findByTitleAndUserId(anyString(), any(Long.class))).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.createTask(taskRequestDto),
                "TaskNotFoundException should be thrown"
        );
        assertEquals("Task not found !!", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(taskRepository, times(1)).findByTitleAndUserId(anyString(), any(Long.class));
    }

    @Test
    @DisplayName("createTaskById should return Task response dto")
    void should_createTaskById_returnTaskResponseDto() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponseDto result = sut.createTaskById(id, taskRequestDto);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(any(User.class));
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("createTaskById should throw UserNotFoundException when user not found")
    void should_createTaskById_throwUserNotFoundException_whenUserNotFound() {
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.createTaskById(id, taskRequestDto),
                "UserNotFoundException should be thrown"
        );

        assertEquals("User not found !!", exception.getMessage());
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("getTaskById should return Task response dto")
    void should_getTaskById_returnTaskResponseDto() {
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));

        TaskResponseDto result = sut.getTaskById(id);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getTaskById should throw TaskNotFoundException when task not found")
    void should_getTaskById_throwTaskNotFoundException_whenTaskNotFound() {
        Long id = 99L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.getTaskById(id),
                "TaskNotFoundException should be thrown"
        );

        assertEquals("Task not found !!", exception.getMessage());
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("deleteTask should delete task")
    void should_deleteTask() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long id = 1L;
        testUser.getTasks().add(testTask);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).delete(any(Task.class));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        sut.deleteTask(id);

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).delete(any(Task.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("deleteTask should throw UserNotFoundException")
    void should_deleteTask_throwUserNotFoundException_whenUserNotFound() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long id = 99L;
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.deleteTask(id),
                "UserNotFoundException should be thrown"
        );

        assertEquals("User not found !!", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(taskRepository, never()).findById(id);
        verify(taskRepository, never()).delete(any(Task.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("deleteTask should throw TaskNotFoundException")
    void should_deleteTask_throwTaskNotFoundException_whenTaskNotFound() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long id = 1L;
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.deleteTask(id),
                "TaskNotFoundException should be thrown"
        );

        assertEquals("Task not found !!" , exception.getMessage());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).delete(any(Task.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("deleteTask should throw TaskUserMismatchException")
    void should_deleteTask_throwTaskUserMismatchException_whenTaskUserMismatch() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long id = 1L;
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));

        TaskUserMismatchException exception = assertThrows(
                TaskUserMismatchException.class,
                () -> sut.deleteTask(id),
                "TaskUserMismatchException should be thrown"
        );

        assertEquals("This task is not assigned to this user !!" , exception.getMessage());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).delete(any(Task.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("isOwner should return true")
    void should_isOwner_returnTrue() {
        Long id = 1L;
        testUser.getTasks().add(testTask);
        when(userRepository.findByUsername( "testUser")).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));

        assertTrue(sut.isOwner(id, "testUser"));
        verify(userRepository, times(1)).findByUsername( "testUser");
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("isOwner should return false")
    void should_isOwner_returnFalse() {
        Long id = 1L;
        when(userRepository.findByUsername( "testUser")).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));

        assertFalse(sut.isOwner(id, "testUser"));
        verify(userRepository, times(1)).findByUsername( "testUser");
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("isOwner should throw UserNotFoundException")
    void should_isOwner_throwUserNotFoundException_whenUserNotFound() {
        Long id = 99L;
        when(userRepository.findByUsername( "unknownUser")).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.isOwner(id, "unknownUser"),
                "UserNotFoundException should be thrown"
        );
        assertEquals("User not found !!", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknownUser");
        verify(taskRepository, never()).findById(id);
    }

    @Test
    @DisplayName("isOwner should throw TaskNotFoundException")
    void should_isOwner_throwTaskNotFoundException_whenTaskNotFound() {
        Long id = 1L;
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.isOwner(id, "testUser"),
                "TaskNotFoundException should be thrown"
        );

        assertEquals("Task not found !!", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("updateTaskStatus should return Task response dto")
    void should_updateTaskStatus() {
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponseDto result = sut.updateTaskStatus(id, TaskStatus.DONE);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(TaskStatus.DONE, result.getStatus());

        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTaskStatus should throw TaskNotFoundException")
    void should_updateTaskStatus_throwTaskNotFoundException_whenTaskNotFound() {
        Long id = 99L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.updateTaskStatus(id, TaskStatus.DONE),
                "TaskNotFoundException should be thrown"
        );

        assertEquals("Task not found !!", exception.getMessage());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTask should return Task response dto")
    void should_updateTask() {
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponseDto result = sut.updateTask(id, taskUpdateDto);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());

        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTask should throw TaskNotFoundException")
    void should_updateTask_throwTaskNotFoundException_whenTaskNotFound() {
        Long id = 99L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> sut.updateTask(id, taskUpdateDto),
                "TaskNotFoundException should be thrown"
        );

        assertEquals("Task not found !!", exception.getMessage());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("getMyTasks should return Task rasponse dto page")
    void should_getMyTasks_returnTaskPage() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUserId(any(Long.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(testTask)));

        Page<TaskResponseDto> result = sut.getMyTasks(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTask.getId(), result.getContent().get(0).getId());
        assertEquals(testTask.getTitle(), result.getContent().get(0).getTitle());
        assertEquals(testTask.getDescription(), result.getContent().get(0).getDescription());
        assertEquals(testTask.getStatus(), result.getContent().get(0).getStatus());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(taskRepository, times(1)).findByUserId(any(Long.class), any(Pageable.class));
    }

    @Test
    @DisplayName("getMyTasks should throw UserNotFoundException")
    void should_getMyTasks_throwUserNotFoundException_whenUserNotFound() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.getMyTasks(Pageable.unpaged()),
                "UserNotFoundException should be thrown"
        );
        assertEquals("User not found !!", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(taskRepository, never()).findByUserId(any(Long.class), any(Pageable.class));
    }

    @Test
    @DisplayName("getAllTasks should return all tasks when status is null")
    void should_getAllTasks_returnTaskPageWhenStatusIsNull() {
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(testTask)));

        Page<TaskResponseDto> result = sut.getAllTasks(null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTask.getId(), result.getContent().get(0).getId());

        verify(taskRepository, times(1)).findAll(any(Pageable.class));
        verify(taskRepository, never()).findByStatus(any(TaskStatus.class), any(Pageable.class));
    }

    @Test
    @DisplayName("getAllTasks should return tasks filtered by status")
    void should_getAllTasks_returnTaskPageWhenStatusIsProvided() {
        when(taskRepository.findByStatus(any(TaskStatus.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(testTask)));

        Page<TaskResponseDto> result = sut.getAllTasks(TaskStatus.DONE, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(taskRepository, times(1)).findByStatus(any(TaskStatus.class), any(Pageable.class));
        verify(taskRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("getTasksByUser should return user tasks when status is null")
    void should_getTasksByUser_returnTaskPageWhenStatusIsNull() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUserId(any(Long.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(testTask)));

        Page<TaskResponseDto> result = sut.getTasksByUser(1L, null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTask.getId(), result.getContent().get(0).getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(taskRepository, times(1)).findByUserId(any(Long.class), any(Pageable.class));
        verify(taskRepository, never()).findByUserIdAndStatus(any(Long.class), any(TaskStatus.class), any(Pageable.class));
    }

    @Test
    @DisplayName("getTasksByUser should return user tasks filtered by status")
    void should_getTasksByUser_returnTaskPageWhenStatusIsProvided() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUserIdAndStatus(any(Long.class), any(TaskStatus.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(testTask)));

        Page<TaskResponseDto> result = sut.getTasksByUser(1L, TaskStatus.DONE, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTask.getId(), result.getContent().get(0).getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(taskRepository, never()).findByUserId(any(Long.class), any(Pageable.class));
        verify(taskRepository, times(1)).findByUserIdAndStatus(any(Long.class), any(TaskStatus.class), any(Pageable.class));
    }

    @Test
    @DisplayName("getTasksByUser should throw UserNotFoundException when user not found")
    void should_getTasksByUser_throwUserNotFoundException_whenUserNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.getTasksByUser(1L, null, Pageable.unpaged()),
                "UserNotFoundException should be thrown"
        );

        assertEquals("User not found !!", exception.getMessage());
        verify(userRepository, times(1)).findById(any(Long.class));
        verify(taskRepository, never()).findByUserId(any(Long.class), any(Pageable.class));
        verify(taskRepository, never()).findByUserIdAndStatus(any(Long.class), any(TaskStatus.class), any(Pageable.class));
    }

    @Test
    @DisplayName("getTasks should return tasks when user_id is provided")
    void should_getTasks_returnTaskPageWhenUserIdIsProvided() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(testUser));
        when(taskRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(new PageImpl<>(List.of(testTask)));

        Page<TaskResponseDto> result = sut.getTasks(1L, TaskStatus.TODO, "sss", Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTask.getId(), result.getContent().get(0).getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(taskRepository, times(1)).findAll(any(Specification.class),any(Pageable.class));
    }

    @Test
    @DisplayName("getTasks should throw UserNotFoundException when user not found")
    void getTasks_should_throwUserNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.getTasks(1L, TaskStatus.TODO, "sss", Pageable.unpaged()),
                "UserNotFoundException should be thrown"
        );

        assertEquals("User not found !!", exception.getMessage());
        verify(userRepository, times(1)).findById(any(Long.class));
        verify(taskRepository, never()).findAll(any(Specification.class),any(Pageable.class));
    }

    @Test
    @DisplayName("getTasks should return tasks when there is no specification")
    void getTasks_should_returnTaskPageWhenSpecificationIsNull() {
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(testTask)));
        Page<TaskResponseDto> result = sut.getTasks(null, null, null, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTask.getId(), result.getContent().get(0).getId());
        verify(taskRepository, times(1)).findAll(any(Pageable.class));
        verify(taskRepository, never()).findAll(any(Specification.class),any(Pageable.class));
        verify(userRepository, never()).findById(any(Long.class));
    }
}