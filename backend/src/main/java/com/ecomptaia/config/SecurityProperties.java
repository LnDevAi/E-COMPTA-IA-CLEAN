package com.ecomptaia.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.security")
public class SecurityProperties {
    
    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    
    public static class Jwt {
        private String secret;
        private long expiration = 86400000; // 24h
        private long refreshExpiration = 604800000; // 7j
        
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpiration() { return expiration; }
        public void setExpiration(long expiration) { this.expiration = expiration; }
        public long getRefreshExpiration() { return refreshExpiration; }
        public void setRefreshExpiration(long refreshExpiration) { this.refreshExpiration = refreshExpiration; }
    }
    
    public static class Cors {
        private String[] allowedOrigins = {"http://localhost:4200"};
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
        private String[] allowedHeaders = {"Authorization", "Content-Type"};
        private boolean allowCredentials = true;
        private long maxAge = 3600;
        
        public String[] getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(String[] allowedOrigins) { this.allowedOrigins = allowedOrigins; }
        public String[] getAllowedMethods() { return allowedMethods; }
        public void setAllowedMethods(String[] allowedMethods) { this.allowedMethods = allowedMethods; }
        public String[] getAllowedHeaders() { return allowedHeaders; }
        public void setAllowedHeaders(String[] allowedHeaders) { this.allowedHeaders = allowedHeaders; }
        public boolean isAllowCredentials() { return allowCredentials; }
        public void setAllowCredentials(boolean allowCredentials) { this.allowCredentials = allowCredentials; }
        public long getMaxAge() { return maxAge; }
        public void setMaxAge(long maxAge) { this.maxAge = maxAge; }
    }
    
    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Cors getCors() { return cors; }
    public void setCors(Cors cors) { this.cors = cors; }
}

