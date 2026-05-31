package com.skyvisa.config;

import java.util.Arrays;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.skyvisa.Security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests(auth -> auth
                                                // Giriş ve Kayıt odaları herkese açık
                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                // 2. YENİ EKLENEN KISIM: Uçuş arama odasını da herkese açık (permitAll)
                                                // yapıyoruz!
                                                .requestMatchers("/api/v1/flights/**").permitAll()
                                                .requestMatchers("/images/**").permitAll()
                                                .requestMatchers("/error").permitAll()
                                                // Geri kalan her yer için Token zorunlu
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // YENİ EKLENEN CORS AYAR MERKEZİ: Hangi portlara ve işlemlere izin verileceği
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                // React'in çalıştığı adrese VIP geçiş hakkı veriyoruz
                configuration.setAllowedOrigins(
                                Arrays.asList("http://localhost:5173", "https://ruyaplanner.vercel.app"));
                // İzin verilen metodlar (GET, POST vb.)
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}