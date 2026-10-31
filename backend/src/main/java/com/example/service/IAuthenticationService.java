package com.example.service;

import com.example.dto.request.LoginRequest;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.AuthenticationResponse;

public interface IAuthenticationService {
    public AuthenticationResponse register(RegisterRequest registerRequest);
    public AuthenticationResponse login(LoginRequest loginRequest);
}
