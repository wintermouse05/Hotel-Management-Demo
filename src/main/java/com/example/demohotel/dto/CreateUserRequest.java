package com.example.demohotel.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String email;
    private String phone;
    private String fullName;
}
