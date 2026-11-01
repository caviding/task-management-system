package com.example.mapper;

import com.example.Enum.Role;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.UserResponseDto;
import com.example.entity.User;

public class UserMapper {

    public static User toEntity(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setRole(Role.USER);
        return user;
    }

    public static UserResponseDto toDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setEmail(user.getEmail());

        return userResponseDto;
    }
}