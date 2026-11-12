package com.example.finance.tracker.auth;

import com.example.finance.tracker.auth.dto.RegisterRequest;
import com.example.finance.tracker.auth.dto.UserResponse;
import com.example.finance.tracker.common.exception.ValidationException;
import com.example.finance.tracker.user.User;
import com.example.finance.tracker.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    public UserResponse register(RegisterRequest request) {
        String username = request.username().trim();

        if (userRepository.existsByUsername(username)) {
            throw new ValidationException("Username " + username + " is already taken");
        }


        String hash = passwordEncoder.encode(request.password());

        User saved = userRepository.save(new User(null, username, hash, Instant.now(clock)));
        return UserResponse.from(saved);
    }
}