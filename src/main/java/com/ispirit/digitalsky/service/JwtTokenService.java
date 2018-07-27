package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.service.api.SecurityTokenService;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.util.Calendar;
import java.util.Date;

public class JwtTokenService implements SecurityTokenService {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    private int jwtExpirationInDays;
    private final Key key;

    public JwtTokenService(ResourceLoader resourceLoader, int jwtExpirationInDays, String jwtKeyStorePath, String jwtKeyStorePassword, String jwtKeyStoreType, String jwtKeyAlias, String jwtKeyPassword) {
        this.jwtExpirationInDays = jwtExpirationInDays;
        try {
            KeyStore keyStore = KeyStore.getInstance(jwtKeyStoreType);
            keyStore.load(resourceLoader.getResource(jwtKeyStorePath).getInputStream(), jwtKeyStorePassword.toCharArray());
            key = keyStore.getKey(jwtKeyAlias, jwtKeyPassword.toCharArray());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.add(Calendar.DAY_OF_MONTH, jwtExpirationInDays);

        return Jwts.builder()
                .setSubject(String.valueOf(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(now.getTime())
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
