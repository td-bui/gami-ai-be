package com.project.gamiai.service.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.gamiai.domain.User;
import com.project.gamiai.dto.request.SigninRequest;
import com.project.gamiai.dto.request.SignupRequest;
import com.project.gamiai.dto.response.AuthResponse;
import com.project.gamiai.exception.InvalidCredentialsException;
import com.project.gamiai.exception.UserAlreadyExistsException;
import com.project.gamiai.repository.UserRepository;
import com.project.gamiai.security.JwtUtil;
import com.project.gamiai.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // When generating a new refresh token (e.g., on login or refresh)
        String refreshToken = UUID.randomUUID().toString();
        Date refreshTokenExpiry = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30 days

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(refreshTokenExpiry);
        user.setLevel(request.getLevel());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, refreshToken, user.getId(), request.getLevel());
    }

    @Override
    public AuthResponse signin(SigninRequest request) {
        User user = userRepository.findByEmailOrUsername(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Username or email not found!"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Password does not match!");
        }
        String refreshToken = UUID.randomUUID().toString();
        Date refreshTokenExpiry = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30 days

        user.setRefreshToken(refreshToken); // Generate new refresh token on login
        user.setRefreshTokenExpiry(refreshTokenExpiry);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, refreshToken, user.getId(), user.getLevel());
    }

    @Override
    public void logout(String refreshToken) {
        // Find the user by refresh token and invalidate it
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElse(null);
        if (user != null) {
            user.setRefreshToken(null);
            userRepository.save(user);
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token!"));

        // Check if refresh token is expired
        if (user.getRefreshTokenExpiry() == null || user.getRefreshTokenExpiry().before(new Date())) {
            throw new InvalidCredentialsException("Refresh token expired!");
        }

        // Generate new tokens
        String newAccessToken = jwtUtil.generateToken(user.getUsername());
        String newRefreshToken = UUID.randomUUID().toString();
        Date newRefreshTokenExpiry = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);

        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(newRefreshTokenExpiry);
        userRepository.save(user);

        return new AuthResponse(newAccessToken, newRefreshToken, user.getId(), user.getLevel());
    }
}