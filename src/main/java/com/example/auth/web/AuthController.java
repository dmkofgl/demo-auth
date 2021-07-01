package com.example.auth.web;

import com.example.auth.domain.TokenMapper;
import com.example.auth.domain.service.TokenService;
import com.example.common.api.contract.AuthApi;
import com.example.common.api.model.token.TokenRequest;
import com.example.common.api.model.token.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApi {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenMapper tokenMapper;

    public ResponseEntity<TokenResponse> generateToken(@RequestBody TokenRequest tokenRequest) {

        return ResponseEntity.ok(tokenMapper.toResponse(tokenService.generateToken(tokenRequest)));
    }

    @Override
    public ResponseEntity<TokenResponse> getToken(String tokenRequest) {
        return ResponseEntity.ok(tokenMapper.toResponse(tokenService.getToken(tokenRequest)));
    }

    public Boolean validateAuthToken(String tokenValue) {
        return tokenService.validateAuthToken(tokenValue);
    }
}
