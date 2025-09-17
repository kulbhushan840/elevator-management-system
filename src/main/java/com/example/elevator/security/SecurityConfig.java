package com.example.elevator.security;

import com.example.elevator.security.JwtFilter;
import com.example.elevator.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // public endpoints
                        .requestMatchers("/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()

                        // passenger endpoints (both PASSENGER + ADMIN allowed)
//                        .requestMatchers("/api/elevators/request",
//                                "/api/elevators/status",
//                                "/api/elevators/logs",
//                                "/api/elevators/optimize").hasAnyRole("PASSENGER", "ADMIN")


                                // Temporarily allow passenger endpoints (no JWT required while testing)
                                .requestMatchers(
                                        "/api/elevators/request",
                                        "/api/elevators/status",
                                        "/api/elevators/simulate",
                                        "/api/elevators/logs",
                                        "/api/elevators/optimize"
                                ).permitAll()
//                        // admin-only endpoints
                        .requestMatchers("/api/elevators/*/assign",
                                "/api/elevators/*/repair").hasRole("ADMIN")

                        // everything else must be authenticated
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
