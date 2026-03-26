package com.cleartrack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}