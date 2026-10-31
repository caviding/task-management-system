package com.example.controller;

import com.example.dto.request.LoginRequest;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.AuthenticationResponse;
import com.example.entity.RootEntity;
import com.example.service.impl.AuthenticationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@Validated
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/register")
    public RootEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest registerRequest){
        return RootEntity.success(authenticationService.register(registerRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public RootEntity<AuthenticationResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        return RootEntity.success(authenticationService.login(loginRequest), HttpStatus.OK);
    }
}
