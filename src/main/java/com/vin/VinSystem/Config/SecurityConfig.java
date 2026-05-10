package com.vin.VinSystem.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.vin.VinSystem.Security.JwtAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                /* ================= OPTIONS ================= */
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                /* ================= AUTH ================= */
                .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/forgot-password",
                        "/api/auth/verify-otp",
                        "/api/auth/reset-password",
                        "/api/auth/register/send-otp",
                        "/api/auth/register/verify",
                        "/uploads/**"
                ).permitAll()

                /* ================= PUBLIC API ================= */

                .requestMatchers(HttpMethod.GET, "/api/branches/**").permitAll()

                .requestMatchers(
                        "/api/cars/**",
                        "/api/models/**",
                        "/api/series/**",
                        "/api/colors/**"
                ).permitAll()

                .requestMatchers("/uploads/**").permitAll()

                /* ================= CHAT BOT PUBLIC ================= */

                .requestMatchers("/api/chat/anon/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/chat").permitAll()
                .requestMatchers("/api/chat/session/*/messages").permitAll()

                /* ================= PAYMENT ================= */

                .requestMatchers(
                        "/api/vnpay/**",
                        "/api/payments/**"
                ).permitAll()

                /* ================= WEBSOCKET ================= */
                // FIX: SockJS dùng nhiều sub-path: /info, /websocket, /xhr,
                // /xhr_streaming, /eventsource, /jsonp, /iframe.html
                // => phải permit toàn bộ /ws/** và /ws-chat/**
                .requestMatchers(
                        "/ws/**",
                        "/ws-chat/**"
                ).permitAll()

                /* ================= SWAGGER ================= */

                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()

                /* ================= STAFF ================= */

                .requestMatchers("/api/staff/**")
                .hasAnyRole("STAFF", "ADMIN")

                .requestMatchers("/api/chat/session/*/staff")
                .authenticated()

                /* ================= ADMIN ================= */

                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                /* ================= APPOINTMENTS ================= */

                .requestMatchers("/api/appointments/**")
                .authenticated()

                /* ================= ALL OTHER ================= */

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/uploads/**");
    }

    /* ================= CORS ================= */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        );

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}