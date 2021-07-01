package com.example.auth.domain.entity;

import com.example.common.api.model.token.TokenRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    private String token;
    private Long userId;
    private Date issuedAt;
    private Date expired;
    private TokenRole role;

}
