package com.megatron44.howudoin.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String userId;

    public JwtAuthenticationToken(String userId, Object credentials, Object principal) {
        super(null);
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // No credentials in this case
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
