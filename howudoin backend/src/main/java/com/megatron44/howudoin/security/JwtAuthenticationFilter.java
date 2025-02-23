package com.megatron44.howudoin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.debug("No Authorization header or invalid format. Skipping JWT validation.");
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            logger.debug("Extracted JWT token: {}", token);

            // Validate the token
            if (!jwtTokenProvider.validateToken(token)) {
                logger.warn("Invalid or expired JWT token.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token.");
                return;
            }

            // Check if SecurityContext already has authentication
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // Extract user ID from the token
                String userId = jwtTokenProvider.getUserIdFromToken(token);
                logger.debug("Authenticated user ID from token: {}", userId);

                // Create and set the authentication object
                JwtAuthenticationToken authentication = new JwtAuthenticationToken(userId, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("SecurityContext updated with authenticated user ID: {}", userId);
            }
        } catch (Exception ex) {
            logger.error("Error validating or processing JWT token", ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error processing authentication token.");
            return;
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
