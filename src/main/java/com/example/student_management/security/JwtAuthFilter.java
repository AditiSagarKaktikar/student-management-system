package com.example.student_management.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.student_management.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private CustomUserDetailsService userDetailsService;
	@Override
	protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

String authHeader = request.getHeader("Authorization");
String token = null;
String username = null;

// Header should look like: "Bearer eyJhbGciOi..."
if (authHeader != null && authHeader.startsWith("Bearer ")) {
    token = authHeader.substring(7);
    try {
        username = jwtUtil.extractUsername(token);
    } catch (Exception e) {
        // invalid/expired token — just treat as unauthenticated, don't crash the filter chain
        username = null;
    }
}

// If we found a username AND nobody is authenticated yet in this request
if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

UserDetails userDetails = userDetailsService.loadUserByUsername(username);

if (jwtUtil.validateToken(token, username)) {

UsernamePasswordAuthenticationToken authToken =
new UsernamePasswordAuthenticationToken(
       userDetails, null, userDetails.getAuthorities());

authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

SecurityContextHolder.getContext().setAuthentication(authToken);
}
}

filterChain.doFilter(request, response); // pass request to the next filter/controller
}
}


