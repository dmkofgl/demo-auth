package com.example.auth.domain;

import com.example.auth.domain.entity.Token;
import com.example.common.api.model.token.TokenResponse;
import org.mapstruct.Mapper;

@Mapper
public interface TokenMapper {
    TokenResponse toResponse(Token token);
}
