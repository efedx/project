package com.security.dtos;

import com.security.entities.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtGenerationRequestDto {
    private String username;
    private Set<Roles> rolesSet;
}
