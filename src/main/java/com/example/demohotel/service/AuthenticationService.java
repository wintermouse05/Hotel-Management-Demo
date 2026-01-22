package com.example.demohotel.service;

import com.example.demohotel.dto.AuthenticationRequest;
import com.example.demohotel.dto.AuthenticationResponse;
import com.example.demohotel.dto.RegisterRequest;
import com.example.demohotel.entity.User;
import com.example.demohotel.repository.UserRepository;
import com.example.demohotel.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Đăng ký user mới
     */
    public AuthenticationResponse register(RegisterRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(User.Role.USER);
        user.setStatus(true);

        userRepository.save(user);

        // Tạo tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000) // Convert to seconds
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Đăng nhập
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Xác thực username và password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Lấy user từ database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Refresh token
     */
    public AuthenticationResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Giữ nguyên refresh token
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
