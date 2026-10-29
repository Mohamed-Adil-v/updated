package com.civicshield.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            @Value("${CIVICSHIELD_ADMIN_USERNAME:}") String username,
            @Value("${CIVICSHIELD_ADMIN_PASSWORD:}") String password) {
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Set CIVICSHIELD_ADMIN_USERNAME and CIVICSHIELD_ADMIN_PASSWORD in the service environment before starting CivicShield.");
        }
        if (password.length() < 12) {
            throw new IllegalStateException("CIVICSHIELD_ADMIN_PASSWORD must be at least 12 characters long.");
        }
        return new InMemoryUserDetailsManager(
            User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build()
        );
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/favicon.ico", "/api/reports", "/api/stats", "/api/auth/me", "/api/csrf", "/error").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/reports/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/reports").permitAll()
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .formLogin(Customizer.withDefaults())
            .logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }
}
