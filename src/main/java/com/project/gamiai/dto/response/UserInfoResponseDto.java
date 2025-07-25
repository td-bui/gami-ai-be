package com.project.gamiai.dto.response;

import lombok.Data;

@Data
public class UserInfoResponseDto {
    private String username;
    private String level;
    private Integer xp;
    private Long rank;
}
