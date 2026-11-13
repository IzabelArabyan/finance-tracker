package com.example.finance.tracker.auth;

import com.example.finance.tracker.auth.dto.LoginRequest;
import com.example.finance.tracker.auth.dto.LoginResponse;
import com.example.finance.tracker.auth.dto.RegisterRequest;
import com.example.finance.tracker.auth.dto.UserResponse;
import com.example.finance.tracker.common.exception.ValidationException;
import com.example.finance.tracker.security.AuthUser;
import com.example.finance.tracker.security.TokenService;
import com.example.finance.tracker.user.User;
import com.example.finance.tracker.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final Clock clock;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       TokenService tokenService,
                       Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.clock = clock;
    }
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.username().trim();

        if (userRepository.existsByUsername(username)) {
            throw new ValidationException("Username " + username + " is already taken");
        }


        String hash = passwordEncoder.encode(request.password());

        User saved = userRepository.save(new User(null, username, hash, Instant.now(clock)));
        return UserResponse.from(saved);
    }
    public LoginResponse login(LoginRequest request) {
        // 1. Check the login: AppUserDetailsService finds the user, BCrypt checks the password
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username().trim(), request.password()));

        //get user principal (userdetails)
        AuthUser user = (AuthUser) authentication.getPrincipal();

        //making token for user
        String token = tokenService.createToken(user.getId(), user.getUsername());
        return new LoginResponse(token, "Bearer", tokenService.expiresInSeconds());
    }
}