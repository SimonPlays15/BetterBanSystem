package me.github.simonplays15.betterbansystem.api.backend.auth;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import me.github.simonplays15.betterbansystem.api.backend.auth.entities.User;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtProvider {

    public static final String SECRET_KEY = generateSecretKey();
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

    public static String generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    public static String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("username", user.getUsername())
                .withClaim("password", user.getPassword())
                .sign(ALGORITHM);
    }

    public static boolean auth(String token, @NotNull User user) {
        DecodedJWT jwt = JWT.require(ALGORITHM).withSubject(user.getUsername()).build().verify(token);
        return jwt.getClaim("username").asString().equals(user.getUsername()) && jwt.getClaim("password").asString().equals(user.getPassword());
    }
}