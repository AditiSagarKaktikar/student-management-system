package com.example.student_management.security;

//import java.awt.RenderingHints.Key;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
	private long expiration;
	
	 private SecretKey getSigningKey() {
	        return Keys.hmacShaKeyFor(secret.getBytes());
	    }
	 public String generateToken(String username, String role) {
		    return Jwts.builder()
		            .subject(username)
		            .claim("role", role)                      // NEW — embed the role in the token payload
		            .issuedAt(new Date())
		            .expiration(new Date(System.currentTimeMillis() + expiration))
		            .signWith(getSigningKey())
		            .compact();
		
	    }
	 public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }

	    public boolean isTokenExpired(String token) {
	        return extractClaim(token, Claims::getExpiration).before(new Date());
	    }

	  
	    public boolean validateToken(String token, String username) {
	        return username.equals(extractUsername(token)) && !isTokenExpired(token);
	    }

	    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
	        Claims claims = Jwts.parser()
	                .verifyWith(getSigningKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	        return resolver.apply(claims);
	    }
}
