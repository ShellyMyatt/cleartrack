package com.cleartrack.controller;

import com.cleartrack.dto.AuthResponse;
import com.cleartrack.dto.LoginRequest;
import com.cleartrack.dto.RegisterRequest;
import com.cleartrack.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.cleartrack.dto.UserProfileResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserProfileResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return authService.getCurrentUserProfile(userDetails.getUsername());
    }
}
