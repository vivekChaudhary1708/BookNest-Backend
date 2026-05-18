package com.booknest.authuserservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

    private String name;
    private String email;
    private String mobile;
    private String password;
    private String role;

    private boolean blocked;
    private Instant createdAt;

    private String resetOtp;
    private Instant resetOtpExpiry;
}