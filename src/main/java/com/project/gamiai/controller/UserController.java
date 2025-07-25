package com.project.gamiai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamiai.dto.response.DashboardResponseDto;
import com.project.gamiai.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves comprehensive dashboard data for a specific user.
     * @param userId The ID of the user.
     * @return A ResponseEntity containing the dashboard data.
     */
    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<DashboardResponseDto> getDashboardData(@PathVariable Integer userId) {
        DashboardResponseDto dashboardData = userService.getDashboardData(userId);
        return ResponseEntity.ok(dashboardData);
    }
}
