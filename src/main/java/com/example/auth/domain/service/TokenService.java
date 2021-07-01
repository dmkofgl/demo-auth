package com.example.auth.domain.service;

import com.example.auth.domain.entity.Token;
import com.example.auth.domain.jwt.JwtTokenProvider;
import com.example.auth.domain.repository.TokenRepository;
import com.example.auth.web.feign.UserClient;
import com.example.common.api.model.token.TokenRequest;
import com.example.common.api.model.token.TokenRole;
import com.example.common.api.model.user.UserPrincipal;
import com.example.common.api.model.user.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class TokenService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TokenRepository tokenRepository;

    public Token generateToken(TokenRequest tokenRequest) {

        Token adminToken = jwtTokenProvider.generateServiceToken();
        UserPrincipal userPrincipal = userClient.getUserByEmailWithToken(tokenRequest.getEmail(), getBearerToken(adminToken)).getBody();
        if (userPrincipal == null) {
            throw new RuntimeException("user principal is null");
        }
        // UserService doesn't have passwordEncoder so password stored in pure format
//        String passwordFromRequest = passwordEncoder.encode(tokenRequest.getPassword());
//        if (!passwordEncoder.matches(passwordFromRequest, userPrincipal.getPassword())) {
        if (!tokenRequest.getPassword().equals(userPrincipal.getPassword())) {
            throw new RuntimeException("password is incorrect");

        }
        Token token = jwtTokenProvider.generateToken(userPrincipal);
        token = tokenRepository.save(token);
        return token;
    }

    private String getBearerToken(Token token) {
        return "Bearer " + token.getToken();
    }

    public Token getToken(String token) {
        if (isServiceToken(token)) {
            return jwtTokenProvider.generateServiceToken();
        }
        return tokenRepository.findById(token).orElseThrow(EntityNotFoundException::new);
    }

    public Boolean validateAuthToken(String tokenValue) {
        if (jwtTokenProvider.isValidToken(tokenValue)) {
            if (isServiceToken(tokenValue)) {
                return true;
            }
            Token adminToken = jwtTokenProvider.generateServiceToken();
            UserResponse user = userClient.getUserWithToken(jwtTokenProvider.getUserIdFromToken(tokenValue), getBearerToken(adminToken)).getBody();
            Token token = tokenRepository.findById(tokenValue).orElseThrow(EntityNotFoundException::new);
            return user != null && user.getId().equals(token.getUserId());
        }
        return false;
    }

    private boolean isServiceToken(String token) {
        return TokenRole.SERVICE.name().equalsIgnoreCase(jwtTokenProvider.getRoleFromToken(token));
    }

}
