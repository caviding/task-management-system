package com.example.service;

import com.example.Enum.Role;
import com.example.Enum.TaskStatus;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.UserResponseDto;
import com.example.dto.request.UserRequestDto;
import com.example.dto.response.UserUpdateResponse;
import com.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
     UserResponseDto createUser(RegisterRequest registerRequest, Role role);
     UserResponseDto getUserById(Long id);
     UserUpdateResponse updateUser(Long id, UserRequestDto userRequestDto);
     Page<UserResponseDto> getAllUsers(Pageable pageable);
     UserResponseDto getProfile();
     boolean isOwner(Long userId, String username);
     void deleteUser(Long id);
}
