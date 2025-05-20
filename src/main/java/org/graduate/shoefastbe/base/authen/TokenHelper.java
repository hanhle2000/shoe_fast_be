package org.graduate.shoefastbe.base.authen;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.graduate.shoefastbe.entity.Account;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class TokenHelper {
    private static final String SECRET_KEY = "yourSecretKeyyourSecretKeyyourSecretKeyyourSecretKeyyourSecretKeyyourSecretKeyyourSecretKeyyourSecretKeyyourSecretKeyyourSecretKeyyourSecretKeyyourSecretKey";
    private static final String USER_ID = "user_id";
    private static final String USERNAME = "username";
    private static final String ROLE = "role";
    private static final String JTI = "jti";

    public static String generateToken(Account userEntity) {
        OffsetDateTime nowOffsetDateTime = OffsetDateTime.now();
        OffsetDateTime expirationOffsetDateTime = nowOffsetDateTime.plusMinutes(100000);

        // Convert OffsetDateTime to Date
        Date now = Date.from(nowOffsetDateTime.toInstant());
        Date expirationDate = Date.from(expirationOffsetDateTime.toInstant());

        String uuid = UUID.randomUUID().toString();
        return Jwts.builder()
                .claim(USER_ID, userEntity.getId())
                .claim(USERNAME, userEntity.getUsername())
                .claim(ROLE, userEntity.getRole())
                .claim(JTI, uuid)
                .setSubject(userEntity.getUsername())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static OffsetDateTime getIatFromToken(String token){
        token = token.substring(7);
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getIssuedAt().toInstant().atOffset(ZoneOffset.UTC);
    }

    public static OffsetDateTime getExpFromToken(String token){
        token = token.substring(7);
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().toInstant().atOffset(ZoneOffset.UTC);
    }

    // getJtiFromToken
    public static String getJtiFromToken(String token) {
        token = token.substring(7);
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(JTI, String.class);
    }


    public static Long getUserIdFromToken(String token) {
        if(Objects.isNull(token)){
            return 0L;
        }
        token = token.substring(7);
        if(token.equals("null") ){
            return 0L;
        }
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(USER_ID, Long.class);
    }

    public static String getRoleFromToken(String token){
        token = token.substring(7);
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(ROLE, String.class);
    }
    public static String getUsernameFromToken(String token){
        token = token.substring(7);
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(USERNAME, String.class);
    }
}
