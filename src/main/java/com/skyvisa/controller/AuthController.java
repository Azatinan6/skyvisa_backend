package com.skyvisa.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.skyvisa.Security.JwtService;
import com.skyvisa.dto.AuthResponseDto;
import com.skyvisa.dto.LoginRequestDto;
import com.skyvisa.dto.RegisterRequestDto;
import com.skyvisa.entity.Role;
import com.skyvisa.entity.User;
import com.skyvisa.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 1. KAYIT OLMA İŞLEMİ
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {

        // E-posta zaten var mı kontrolü
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, "Bu e-posta zaten kullanılıyor!"));
        }

        User user = new User();
        user.setEmail(registerRequestDto.getEmail());
        user.setFullName(registerRequestDto.getFullName());
        user.setPasswordHash(passwordEncoder.encode(registerRequestDto.getPassword()));

        user.setRole(Role.USER);

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponseDto(token, "Kayıt başarılı! Lütfen giriş yapın."));
    }

    // 2. GİRİŞ YAPMA İŞLEMİ
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        Optional<User> optionalUser = userRepository.findByEmail(loginRequestDto.getEmail());

        if (optionalUser.isEmpty()) {
            // 2. HATA DÜZELTİLDİ: AuthController yerine AuthResponseDto yazıldı ve noktalı virgül eklendi
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, "Kullanıcı bulunamadı!"));
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, "Hatalı şifre!"));
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponseDto(token, "Giriş başarılı!"));
    }
}