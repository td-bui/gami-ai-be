package com.project.gamiai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamiai.dto.request.SigninRequest;
import com.project.gamiai.dto.request.SignupRequest;
import com.project.gamiai.dto.response.AuthResponse;
import com.project.gamiai.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        return userService.signup(request);
    }

    @PostMapping("/signin")
    public AuthResponse signin(@Valid @RequestBody SigninRequest request) {
        return userService.signin(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return userService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public void logout(@RequestParam("refreshToken") String refreshToken) {
        userService.logout(refreshToken);
    }
}