package com.example.auth.domain.repository;

import com.example.auth.domain.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
}
