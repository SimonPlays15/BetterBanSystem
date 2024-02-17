package me.github.simonplays15.betterbansystem.api.backend.security;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * WebSecurityConfig is a class that provides configuration for web security.
 * It is annotated with @Configuration and @EnableWebSecurity, which indicates that it is a configuration class and enables web security respectively.
 * It also defines a method that returns a SecurityFilterChain, which is responsible for configuring the security for HTTP requests.
 * The configureGlobal method is used to configure authentication in memory with a single user.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * Returns a SecurityFilterChain that configures the security for HTTP requests.
     *
     * @param http the HttpSecurity object used to configure the security
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring the security
     */
    @Bean
    protected SecurityFilterChain mySecurityFilterChain(@NotNull HttpSecurity http) throws Exception {

        http.httpBasic(basic -> {
            basic.setBuilder(http);
        }).authorizeHttpRequests(request -> request.anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }

    /**
     * Configures authentication in memory with a single user.
     *
     * @param auth the AuthenticationManagerBuilder for configuring authentication.
     * @throws Exception if an error occurs while configuring authentication.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }

}
