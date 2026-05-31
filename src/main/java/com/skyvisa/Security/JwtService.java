package com.skyvisa.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Şifreleme için gizli imzamız (En az 256-bit / 32 karakter olmalı)
    // DİKKAT: Bu anahtarı kimse bilmemeli. Gerçek projelerde kodun içine değil,
    // application.properties içine yazılır.
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // 1. Token'dan Kullanıcı Adını (Bizim sistemde Email) Çekme
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Kullanıcı için yeni Token (Bilet) Üretme
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // Ekstra bilgi eklemek istersek (örn: Kullanıcının ID'si)
                .setSubject(userDetails.getUsername()) // Ana bilgi (Email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Ne zaman üretildi?
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 Saat geçerli
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Bizim gizli anahtarımızla imzala
                .compact();
    }

    // 3. Token'ın Geçerli Olup Olmadığını Kontrol Etme
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Hem token'daki email kullanıcının emailiyle eşleşmeli, hem de süresi dolmamış
        // olmalı
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}