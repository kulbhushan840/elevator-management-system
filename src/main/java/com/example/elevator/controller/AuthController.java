package com.example.elevator.controller;

import com.example.elevator.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String user = body.get("username");
        String pass = body.get("password");

        if ("admin".equals(user) && "adminpass".equals(pass)) {
            return ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(user, "ADMIN")));
        } else if ("user".equals(user) && "userpass".equals(pass)) {
            return ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(user, "PASSENGER")));
        }

        return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
    }
}
