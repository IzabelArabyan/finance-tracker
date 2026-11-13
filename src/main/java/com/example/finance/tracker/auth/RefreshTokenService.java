package com.example.finance.tracker.auth;

import com.example.finance.tracker.auth.repository.RefreshTokenRepository;
import com.example.finance.tracker.user.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;


@Service
@Transactional
public class RefreshTokenService {


    private static final long EXPIRATION_DAYS = 7;

    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, Clock clock) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.clock = clock;
    }


    public String create(User user) {

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(EXPIRATION_DAYS, ChronoUnit.DAYS);
        refreshTokenRepository.save(new RefreshToken(null, token, user, expiresAt, now));

        return token;
    }


    public User use(String token) {
        Optional<RefreshToken> found = refreshTokenRepository.findByToken(token);
        if (found.isEmpty()) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        RefreshToken refreshToken = found.get();
        if (refreshToken.getExpiresAt().isBefore(Instant.now(clock))) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        //each refresh token works only once
        refreshTokenRepository.delete(refreshToken);
        return refreshToken.getUser();
    }

    //for log out
    public void delete(String token) {
        Optional<RefreshToken> found = refreshTokenRepository.findByToken(token);
        if (found.isPresent()) {
            refreshTokenRepository.delete(found.get());
        }
    }
}