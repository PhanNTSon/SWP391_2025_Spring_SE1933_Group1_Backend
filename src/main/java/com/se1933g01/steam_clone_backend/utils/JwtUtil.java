package com.se1933g01.steam_clone_backend.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.security.Key;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * @author Phan NT Son
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    /**
     * The JJWT library needs a Key instance of sufficient length
     * to compute and check the signature.
     * 
     * @param secretKey above.
     * @return javax.crypto.SecretKey implementing HMAC-SHA256
     */
    private final Key getSignedKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        System.out.println("[JwtUtil] secretKey.length=" + keyBytes.length);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("[JwtUtil] derived key algorithm=" + key.getAlgorithm());
        return key;
    }

    /**
     * Generate a JWT Token.
     * 
     * @param username
     * @param userId
     * @param role
     * @return compact JWT string
     */
    public String generateToken(String username, Long userId, String role) {
        String jwt = Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256)
                .compact();
        System.out.println("[JwtUtil] generated JWT: " + jwt);
        return jwt;
    }

    /**
     * Validate a Token.
     * 
     * @param token
     * @return Claims object (all the data inside the token) if valid
     * @exception Throws JwtException (ExpiredJwtException, etc.) if invalid/expired
     */
    public Claims parseClaims(String token) {
        System.out.println("[JwtUtil] parseClaims got token: " + token);
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignedKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            System.out.println("[JwtUtil] parseClaims failed: " + e.getClass().getSimpleName()
                    + " — " + e.getMessage());
            throw new RuntimeException("Invalid or expired Token");
        }
    }

    /**
     * 
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

}
