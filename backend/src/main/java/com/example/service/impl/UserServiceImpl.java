package com.example.service.impl;

import java.util.Optional;

import com.example.Enum.Role;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.UserResponseDto;
import com.example.dto.response.UserUpdateResponse;
import com.example.entity.User;
import com.example.dto.request.UserRequestDto;
import com.example.exception.UserAlreadyExistsException;
import com.example.mapper.UserMapper;
import com.example.repository.TaskRepository;
import com.example.service.IUserService;
import com.example.repository.UserRepository;
import com.example.service.jwt.JWTService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Override
    public UserResponseDto createUser(RegisterRequest registerRequest, Role role) {

        if (userRepository.existsByUsername(registerRequest.getUsername())){
            throw new UserAlreadyExistsException("Username already exists !!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new UserAlreadyExistsException("Email already exists !!");
        }

        User user = UserMapper.toEntity(registerRequest);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toDto);
    }

    @Override
    public UserResponseDto getProfile() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found !!"));

        return UserMapper.toDto(user);
    }

    @Override
    public boolean isOwner(Long userId, String username) {
        return userRepository.existsByIdAndUsername(userId, username);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found !!");
        }
        taskRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()){
            throw new UserNotFoundException("User not found !!");
        }
        return UserMapper.toDto(optional.get());
    }

    @Override
    public UserUpdateResponse updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found !!"));
        if(userRepository.existsByUsername(userRequestDto.getUsername()) && !user.getUsername().equals(userRequestDto.getUsername())){
            throw new UserAlreadyExistsException("Username already exists !!");
        }
        if(userRepository.existsByEmail(userRequestDto.getEmail()) && !user.getEmail().equals(userRequestDto.getEmail())){
            throw new UserAlreadyExistsException("Email already exists !!");
        }

        String authenticatedUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        String oldUsername = user.getUsername();

        boolean updatingOwnProfile =
                oldUsername.equals(authenticatedUsername);

        boolean usernameChanged =
                !oldUsername.equals(userRequestDto.getUsername());

        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());

        User savedUser = userRepository.save(user);
        String newToken = null;

        if (updatingOwnProfile && usernameChanged){
            newToken = jwtService.generateToken(userRequestDto.getUsername());
        }
        return UserUpdateResponse.builder()
                .user(UserMapper.toDto(savedUser))
                .token(newToken)
                .build();
    }
}
