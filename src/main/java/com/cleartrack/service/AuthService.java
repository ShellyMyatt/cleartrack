package com.cleartrack.service;

import com.cleartrack.dto.AuthResponse;
import com.cleartrack.security.CustomUserDetailsService;
import com.cleartrack.security.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import com.cleartrack.dto.LoginRequest;
import com.cleartrack.dto.RegisterRequest;
import com.cleartrack.entity.Role;
import com.cleartrack.entity.SystemRoleName;
import com.cleartrack.entity.User;
import com.cleartrack.exception.BadRequestException;
import com.cleartrack.exception.ResourceNotFoundException;
import com.cleartrack.repository.RoleRepository;
import com.cleartrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cleartrack.dto.UserProfileResponse;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public void register(RegisterRequest request) {

        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        // Find USER role
        Role userRole = roleRepository.findByName(SystemRoleName.USER.name())
                .orElseThrow(() -> new ResourceNotFoundException("Default role USER not found"));

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        user.getRoles().add(userRole);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid username or password");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken);
    }

    public UserProfileResponse getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }
}