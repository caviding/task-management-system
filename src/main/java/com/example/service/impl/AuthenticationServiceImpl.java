package com.example.service.impl;

import com.example.Enum.Role;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.AuthenticationResponse;
import com.example.entity.User;
import com.example.exception.UserAlreadyExistsException;
import com.example.repository.UserRepository;
import com.example.service.IAuthenticationService;
import com.example.service.jwt.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @Override
    public AuthenticationResponse register(RegisterRequest registerRequest){
        if (userRepository.existsByUsername(registerRequest.getUsername())){
            throw new UserAlreadyExistsException("Username already exists !!");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new UserAlreadyExistsException("Email already exists !!");
        }
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .email(registerRequest.getEmail())
                .build();
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user.getUsername());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(loginRequest.getUsername());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}