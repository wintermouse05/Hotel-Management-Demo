package com.example.demohotel.Controller;

import com.example.demohotel.dto.AuthenticationRequest;
import com.example.demohotel.dto.AuthenticationResponse;
import com.example.demohotel.dto.RegisterRequest;
import com.example.demohotel.dto.ResponseDTO;
import com.example.demohotel.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Đăng ký tài khoản mới
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<AuthenticationResponse>> register(
            @RequestBody RegisterRequest request
    ) {
        try {
            AuthenticationResponse response = authenticationService.register(request);
            return ResponseEntity.ok(
                    ResponseDTO.<AuthenticationResponse>builder()
                            .code(200)
                            .message("Registration successful")
                            .data(response)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            String message = "Username or email already exists";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("username")) {
                    message = "Username already exists";
                } else if (e.getMessage().contains("email")) {
                    message = "Email already exists";
                }
            }
            return ResponseEntity.badRequest().body(
                    ResponseDTO.<AuthenticationResponse>builder()
                            .code(400)
                            .message(message)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.<AuthenticationResponse>builder()
                            .code(400)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Đăng nhập
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthenticationResponse>> login(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(
                    ResponseDTO.<AuthenticationResponse>builder()
                            .code(200)
                            .message("Login successful")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    ResponseDTO.<AuthenticationResponse>builder()
                            .code(401)
                            .message("Invalid username or password")
                            .build()
            );
        }
    }

    /**
     * Refresh access token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO<AuthenticationResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Invalid refresh token");
            }

            String refreshToken = authHeader.substring(7);
            AuthenticationResponse response = authenticationService.refreshToken(refreshToken);

            return ResponseEntity.ok(
                    ResponseDTO.<AuthenticationResponse>builder()
                            .code(200)
                            .message("Token refreshed successfully")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    ResponseDTO.<AuthenticationResponse>builder()
                            .code(401)
                            .message("Invalid or expired refresh token")
                            .build()
            );
        }
    }
}
