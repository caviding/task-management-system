package com.example.service.impl;

import java.util.Optional;

import com.example.Enum.Role;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.UserResponseDto;
import com.example.entity.User;
import com.example.dto.request.UserRequestDto;
import com.example.mapper.UserMapper;
import com.example.service.IUserService;
import com.example.repository.UserRepository;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto createUser(RegisterRequest registerRequest, Role role) {
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null) {
            throw new UserNotFoundException("User not found !!");
        }
        return UserMapper.toDto(userRepository.findByUsername(username).get());
    }

    @Override
    public boolean isOwner(Long userId, String username) {
        return userRepository.existsByIdAndUsername(userId, username);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found !!");
        }
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
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found !!"));
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());
        return UserMapper.toDto(userRepository.save(user));
    }
}
