package com.booknest.authuserservice.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String mobile;
    private String password;
    /**
     * Expected values: CUSTOMER | ADMIN.
     * Frontend only allows CUSTOMER registration.
     */
    private String role;
}