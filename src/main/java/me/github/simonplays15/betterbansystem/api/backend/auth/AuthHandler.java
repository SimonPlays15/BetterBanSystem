package me.github.simonplays15.betterbansystem.api.backend.auth;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.*;
import io.javalin.security.BasicAuthCredentials;
import me.github.simonplays15.betterbansystem.api.backend.auth.entities.User;
import me.github.simonplays15.betterbansystem.api.backend.controller.UserController;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AuthHandler {
    /**
     * Handles access to a protected resource based on the provided context.
     *
     * @param context The context containing information about the request.
     * @throws BadRequestResponse   if the request is invalid.
     * @throws UnauthorizedResponse if the token is invalid.
     * @throws ForbiddenResponse    if the user is not found or password is incorrect.
     */
    @Contract(pure = true)
    public static void handleAccess(@NotNull Context context) {
        String jwtToken = context.queryParam("token");
        String userid = context.sessionAttribute("userid");

        if (jwtToken != null && userid != null) {
            context.header(Header.AUTHORIZATION, "Bearer " + jwtToken);
            User user = UserController.getUserByUserId(userid);
            if (user == null)
                throw new BadRequestResponse();
            if (!JwtProvider.auth(jwtToken, user))
                throw new UnauthorizedResponse("Invalid token");
            throw new OkResponse("{status: 200, message: \"OK\"}");
        }
        context.header(Header.WWW_AUTHENTICATE, "Basic");

        BasicAuthCredentials credentials = context.basicAuthCredentials();
        // User init
        if (credentials != null) {
            User user = UserController.getUserByUsername(credentials.getUsername());
            if (user == null)
                throw new ForbiddenResponse("User not found");
            if (!credentials.getPassword().equals(user.getPassword()))
                throw new ForbiddenResponse("Wrong password");

            jwtToken = JwtProvider.generateToken(user);
            if (user.getUserid() == null) {
                userid = JwtProvider.generateSecretKey();
                user.setUserid(userid);
            } else {
                userid = user.getUserid();
            }
            user.setToken(jwtToken);
            context.sessionAttribute("userid", userid);
            UserController.updateUser(user);
            try {
                throw new OkResponse(new ObjectMapper().writeValueAsString(Map.of("userid", userid, "token", jwtToken)));
            } catch (JsonProcessingException e) {
                throw new InternalServerErrorResponse();
            }
        }
        throw new UnauthorizedResponse();
    }

}
