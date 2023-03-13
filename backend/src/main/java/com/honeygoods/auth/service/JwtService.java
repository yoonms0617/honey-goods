package com.honeygoods.auth.service;

import com.honeygoods.auth.exception.ExpiredTokenException;

import com.honeygoods.auth.exception.InvalidTokenException;
import com.honeygoods.auth.exception.InvalidTokenSecretKeyException;
import com.honeygoods.common.error.exception.ErrorType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;

import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;

    private final long accessTokenExpireInMilliseconds;

    private final long refreshTokenExpireInMilliseconds;

    public JwtService(@Value("${auth.jwt.secret-key}") String secretKey,
                      @Value("${auth.jwt.access-token.expire-length}") long accessTokenExpireInMilliseconds,
                      @Value("${auth.jwt.refresh-token.expire-length}") long refreshTokenExpireInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpireInMilliseconds = accessTokenExpireInMilliseconds;
        this.refreshTokenExpireInMilliseconds = refreshTokenExpireInMilliseconds;
    }

    public String createAccessToken(Long id, String email, String role) {
        return createToken(id, email, role, accessTokenExpireInMilliseconds);
    }

    public String createRefreshToken(Long id, String email, String role) {
        return createToken(id, email, role, refreshTokenExpireInMilliseconds);
    }

    public Claims getPayloadFormToken(String token) {
        return getClaims(token).getBody();
    }

    public void validateToken(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            checkTokenExpired(claims.getBody().getExpiration());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(ErrorType.INVALID_TOKEN.getCode());
        }
    }

    private String createToken(Long id, String email, String role, long expireInMilliseconds) {
        Date iat = new Date();
        Date exp = new Date(iat.getTime() + expireInMilliseconds);
        return Jwts.builder()
                .setSubject(Long.toString(id))
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Jws<Claims> getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (IllegalArgumentException | MalformedJwtException e) {
            throw new InvalidTokenException(ErrorType.INVALID_TOKEN.getCode());
        } catch (SignatureException e) {
            throw new InvalidTokenSecretKeyException(ErrorType.INVALID_TOKEN_SECRET_KEY.getCode());
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException(ErrorType.EXPIRED_TOKEN.getCode());
        }
    }

    private void checkTokenExpired(Date exp) {
        if (exp.before(new Date())) {
            throw new ExpiredTokenException(ErrorType.EXPIRED_TOKEN.getCode());
        }
    }

}
