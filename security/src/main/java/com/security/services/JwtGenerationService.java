package com.security.services;

import com.security.entities.Roles;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtGenerationService implements com.security.interfaces.JwtGenerationService {

    private final SecretKey signingKey;

    //-----------------------------------------------------------------------------------------------

    Date now = new Date(System.currentTimeMillis());
    Date expiration = new Date(now.getTime() + 999999999); // 360000000


    /**
     * Generates a JSON Web Token (JWT) for the given username and set of roles.
     *
     * @param username  The username associated with the JWT.
     * @param rolesSet  The set of roles associated with the JWT.
     * @return The generated JWT as a string.
     */
    @Override
    public String generateJwt(String username, Set<Roles> rolesSet) {
        return Jwts.builder()
                .setIssuer("Toyota Project")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .claim("username", username)
                .claim("authorities", rolesSetToString(rolesSet))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * Converts a set of Roles to a comma-separated string representation.
     *
     * @param rolesSet  The set of Roles to convert.
     * @return The comma-separated string representation of the roles.
     */
    private String rolesSetToString (Set<Roles> rolesSet) {
        Set<String> roleStringSet = new HashSet<>(); // set is an interface, hashset is an implementation
        for(Roles role: rolesSet) {
            roleStringSet.add(role.getRoleName());
        }
        return String.join(",", roleStringSet);
    }
}
