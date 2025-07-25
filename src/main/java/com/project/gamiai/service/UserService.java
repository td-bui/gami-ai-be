package com.project.gamiai.service;

import com.project.gamiai.dto.request.SigninRequest;
import com.project.gamiai.dto.request.SignupRequest;
import com.project.gamiai.dto.response.AuthResponse;
import com.project.gamiai.dto.response.DashboardResponseDto;

public interface UserService {
    AuthResponse signup(SignupRequest request);
    AuthResponse signin(SigninRequest request);
    void logout(String refreshToken);
    AuthResponse refreshToken(String refreshToken);
    DashboardResponseDto getDashboardData(Integer userId);
}