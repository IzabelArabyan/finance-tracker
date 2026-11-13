package com.example.finance.tracker.security;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Service
public class TokenService {

    //token is valid ultil this minutes
    private static final long EXPIRATION_MINUTES = 15;

    private final JwtEncoder jwtEncoder;
    private final Clock clock;

    public TokenService(JwtEncoder jwtEncoder, Clock clock) {
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
    }


    public String createToken(Long userId, String username) {
        Instant now = Instant.now(clock);

        //what token says
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("finance-tracker")                                       // who made it
                .issuedAt(now)                                                   // when it was made
                .expiresAt(now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES))     // until when it works
                .subject(String.valueOf(userId))                                // WHO it is for: the user id
                .claim("username", username)
                .build();

        //having header with same algo
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        //making token with our secret key
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long expiresInSeconds() {
        return EXPIRATION_MINUTES * 60;
    }
}