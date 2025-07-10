package com.brogrammerbrigade.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class JwtTokenService {
    private static JwtTokenService instance;
    private final String jwtSecret;
    private final long jwtExpiration;

    private JwtTokenService() {
        // Load these from a properties file or environment variables
        this.jwtSecret = "Fb6lL2mBrNnRgw9UhtFDnlpb4Uedo9BQ";
        this.jwtExpiration = 86400000; // 1 day in milliseconds
    }

    public static synchronized JwtTokenService getInstance() {
        if (instance == null) {
            instance = new JwtTokenService();
        }
        return instance;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String username, BigInteger userId, String role, String userType, List<String> authorities, String department) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .claim("userType", userType)
                .claim("authorities", authorities)
                .claim("department", department)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public BigInteger getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return new BigInteger(claims.get("userId").toString());
    }

    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public List<String> getAuthoritiesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("authorities", List.class);
    }
    public String getUserTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userType", String.class);
    }
    public String getDepartmentFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("department", String.class);
    }
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
}