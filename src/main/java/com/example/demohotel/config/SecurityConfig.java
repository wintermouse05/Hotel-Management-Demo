package com.example.demohotel.config;

import com.example.demohotel.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF cho REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Cấu hình authorization
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập công khai
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/index").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        .requestMatchers("/hotels", "/hotels/**").permitAll()
                        .requestMatchers("/rooms", "/rooms/**").permitAll()
                        .requestMatchers("/users", "/users/**").permitAll()
                        .requestMatchers("/bookings", "/bookings/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Public API endpoints (hotels, rooms)
                        .requestMatchers("/api/v1/hotels/**").permitAll()
                        .requestMatchers("/api/v1/rooms/**").permitAll()

                        // API endpoints yêu cầu authentication
                        .requestMatchers("/api/v1/bookings/**").authenticated()
                        .requestMatchers("/api/v1/users/**").authenticated()

                        // Admin only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Tất cả request khác cần authenticated
                        .anyRequest().authenticated()
                )

                // Sử dụng stateless session (không lưu session)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Cấu hình authentication provider
                .authenticationProvider(authenticationProvider())

                // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
