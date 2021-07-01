package com.example.auth.domain.jwt;

import com.example.auth.domain.entity.Token;
import com.example.common.api.model.token.TokenRole;
import com.example.common.api.model.user.UserPrincipal;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtTokenProvider {

    @Value("${security.jwt.secret}")
    private String secret;
    @Value("${security.jwt.token-expiration}")
    private long VALID_TOKEN_TIME;


    public Token generateToken(UserPrincipal accountPrincipal) {
        return generate(accountPrincipal.getId(), accountPrincipal.getEmail(), TokenRole.USER);
    }

    public Token generateServiceToken() {
        return generate(null, TokenRole.SERVICE.name(), TokenRole.SERVICE);
    }

    private Token generate(Long userId, String subject, TokenRole role) {
        Date iat = new Date();
        Date exp = new Date(iat.getTime() + VALID_TOKEN_TIME);
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claim.USER_ID_KEY.name(), userId);
        claims.put(Claim.ROLE.name(), role);

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        return new Token(jwt, userId, iat, exp, role);
    }


    public Long getUserIdFromToken(String token) {
        if (!isValidToken(token)) {
            throw new RuntimeException("Invalid JWT token");
        }

        final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        Long userId;
        try {
            userId = Long.valueOf(claims.get(Claim.USER_ID_KEY.name()).toString());
        } catch (Exception e) {
            log.debug(e.getLocalizedMessage());
            userId = null;
        }
        return userId;
    }

    public String getRoleFromToken(String token) {
        if (!isValidToken(token)) {
            throw new RuntimeException("Invalid JWT token");
        }

        final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.get(Claim.ROLE.name()).toString();
    }


    public boolean isValidToken(String jwt) {
        String bearerPrefix = "Bearer ";
        try {
            if (jwt.startsWith(bearerPrefix)) {
                jwt = jwt.substring(bearerPrefix.length());
            }
            Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
