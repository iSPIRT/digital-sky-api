package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.service.api.SecurityTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.RsaProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Date;

public class JwtTokenService implements SecurityTokenService {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    private int jwtExpirationInMs;
    private final Key key;

    public JwtTokenService(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new ClassPathResource("keystore.jks").getInputStream(),"cacms789".toCharArray());
            key = keyStore.getKey("tomcat-localhost", "cacms789".toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.RS256, key)
                .compact();
    }

    @Override
    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}
