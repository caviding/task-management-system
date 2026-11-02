package com.example.service;

import com.example.Enum.Role;
import com.example.dto.request.RegisterRequest;
import com.example.dto.request.UserRequestDto;
import com.example.dto.response.UserResponseDto;
import com.example.dto.response.UserUpdateResponse;
import com.example.entity.User;
import com.example.exception.UserAlreadyExistsException;
import com.example.exception.UserNotFoundException;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import com.example.service.impl.UserServiceImpl;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl sut;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TaskRepository taskRepository;

    private User testUser;
    private RegisterRequest registerRequest;
    private UserRequestDto userRequestDto;

    @BeforeEach
    public void setup() {

        testUser = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@gmail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        userRequestDto = UserRequestDto.builder()
                .username("testUser")
                .email("test@gmail.com")
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser");
        registerRequest.setEmail("test@gmail.com");
        registerRequest.setPassword("rawPassword");
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getUserById should return User response dto")
    void should_getUserById_returnUserResponseDto() {

        Long id = 1L;

        when(userRepository.findById(id))
                .thenReturn(Optional.of(testUser));

        UserResponseDto result = sut.getUserById(id);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getUserById should throw UserNotFoundException when user not found")
    void should_throwUserNotFoundExceptionWhenUserNotFound() {

        Long id = 5L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.getUserById(id),
                "UserNotFoundException should be thrown"
        );

        assertEquals("User not found !!", exception.getMessage());

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getAllUsers should return all users page")
    void should_getAllUsers_returnAllUsersPage() {

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testUser)));

        Page<UserResponseDto> result =
                sut.getAllUsers(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(
                testUser.getUsername(),
                result.getContent().get(0).getUsername()
        );
        assertEquals(
                testUser.getEmail(),
                result.getContent().get(0).getEmail()
        );
        assertEquals(
                testUser.getId(),
                result.getContent().get(0).getId()
        );

        verify(userRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("getAllUsers should return empty page")
    void should_getAllUsers_returnEmptyPageWhenNoUsersExists() {

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<UserResponseDto> result =
                sut.getAllUsers(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(userRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("createUser should save user and return User response dto")
    void should_createUser_returnUserResponseDto() {

        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        UserResponseDto result =
                sut.createUser(registerRequest, Role.USER);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());

        verify(userRepository, times(1))
                .save(any(User.class));

        verify(passwordEncoder, times(1))
                .encode(anyString());
    }

    @Test
    @DisplayName("createUser should throw UserAlreadyExistsException when user already exists")
    void should_createUser_throwUserAlreadyExistsExceptionwhenUserAlreadyExists() {

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenThrow(
                        new UserAlreadyExistsException(
                                "User already exists !!"
                        )
                );

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> sut.createUser(registerRequest, Role.USER),
                "UserAlreadyExistsException should be thrown"
        );

        assertEquals(
                "User already exists !!",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .save(any(User.class));

        verify(passwordEncoder, times(1))
                .encode(anyString());
    }

    @Test
    @DisplayName("getProfile should return User response dto")
    void should_getProfile_returnUserResponseDto() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        null,
                        List.of()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(testUser));

        UserResponseDto result = sut.getProfile();

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());

        verify(userRepository, times(1))
                .findByUsername(anyString());
    }

    @Test
    @DisplayName("getProfile should throw UserNotFoundException")
    void should_getProfile_throwUserNotFoundException() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn(null);

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.getProfile(),
                "UserNotFoundException should be thrown"
        );

        assertEquals(
                "User not found !!",
                exception.getMessage()
        );

        verify(userRepository, never())
                .findByUsername(anyString());
    }

    @Test
    @DisplayName("deleteUser should delete user's tasks and then delete user")
    void should_deleteUser_deleteUserById() {

        Long id = 1L;

        when(userRepository.existsById(id))
                .thenReturn(true);

        sut.deleteUser(id);

        verify(userRepository, times(1))
                .existsById(id);

        verify(taskRepository, times(1))
                .deleteAllByUserId(id);

        verify(userRepository, times(1))
                .deleteById(id);
    }

    @Test
    @DisplayName("deleteUser should throw UserNotFoundException")
    void should_deleteUser_throwUserNotFoundException() {

        Long id = 99L;

        when(userRepository.existsById(id))
                .thenReturn(false);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.deleteUser(id)
        );

        assertEquals(
                "User not found !!",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .existsById(id);

        verify(taskRepository, never())
                .deleteAllByUserId(anyLong());

        verify(userRepository, never())
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("isOwner should return true")
    void should_isOwner_returnTrue() {

        Long id = 1L;
        String username = "testUser";

        when(userRepository.existsByIdAndUsername(id, username))
                .thenReturn(true);

        assertTrue(sut.isOwner(id, username));

        verify(userRepository, times(1))
                .existsByIdAndUsername(id, username);
    }

    @Test
    @DisplayName("isOwner should return false")
    void should_isOwner_returnFalse() {

        Long id = 1L;
        String username = "otherUser";

        when(userRepository.existsByIdAndUsername(id, username))
                .thenReturn(false);

        assertFalse(sut.isOwner(id, username));

        verify(userRepository, times(1))
                .existsByIdAndUsername(id, username);
    }

    @Test
    @DisplayName("updateUser should return updated User response dto")
    void should_updateUser_returnUserResponseDto() {

        Long id = 1L;

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        null,
                        List.of()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(testUser));

        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        UserUpdateResponse result =
                sut.updateUser(id, userRequestDto);

        assertNotNull(result);
        assertNotNull(result.getUser());

        assertEquals(
                testUser.getId(),
                result.getUser().getId()
        );

        assertEquals(
                testUser.getUsername(),
                result.getUser().getUsername()
        );

        verify(userRepository, times(1))
                .findById(id);

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    @DisplayName("updateUser should throw UserNotFoundException")
    void should_updateUser_throwUserNotFoundException() {

        Long id = 99L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> sut.updateUser(id, userRequestDto),
                "UserNotFoundException should be thrown"
        );

        verify(userRepository, times(1))
                .findById(id);

        verify(userRepository, never())
                .save(any(User.class));
    }
}