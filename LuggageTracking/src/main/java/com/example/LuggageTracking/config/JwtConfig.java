package com.example.LuggageTracking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.header:Authorization}")
    private String header;
    
    @Value("${jwt.prefix:Bearer }")
    private String prefix;
    
    // Getters
    public String getSecret() {
        return secret;
    }
    
    public Long getExpiration() {
        return expiration;
    }
    
    public String getHeader() {
        return header;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    // Setters
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
