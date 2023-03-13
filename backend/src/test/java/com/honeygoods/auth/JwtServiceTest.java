package com.honeygoods.auth;

import com.honeygoods.auth.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class JwtServiceTest {

    private static final Long ID = 1L;

    private static final String EMAIL = "yoon@test.com";

    private static final String ROLE = "ROLE_MEMBER";

    private static final String INVALID_SECRET_KEY = "wrong-invalid-sercret-key-wrong-invalid-sercret-key-1q2w3e4r!";

    @Value("${auth.jwt.secret-key}")
    private String secretKey;

    @Value("${auth.jwt.access-token.expire-length}")
    private long accessTokenExpireInMilliseconds;

    @Value("${auth.jwt.refresh-token.expire-length}")
    private long refreshTokenExpireInMilliseconds;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("유효한 access token을 생성한다.")
    void create_access_token_test() {
        String accessToken = jwtService.createAccessToken(ID, EMAIL, ROLE);

        assertThat(accessToken).isNotNull();
    }

    @Test
    @DisplayName("유효한 refresh token을 생성한다.")
    void create_refresh_token_test() {
        String refreshToken = jwtService.createRefreshToken(ID, EMAIL, ROLE);

        assertThat(refreshToken).isNotNull();
    }

    @Test
    @DisplayName("유효한 토큰으로 payload를 조회한다.")
    void get_payload_valid_token_test() {
        String accessToken = jwtService.createAccessToken(ID, EMAIL, ROLE);

        Claims payload = jwtService.getPayloadFormToken(accessToken);

        assertThat(Long.valueOf(payload.getSubject())).isEqualTo(ID);
        assertThat((String) payload.get("email")).isEqualTo(EMAIL);
        assertThat((String) payload.get("role")).isEqualTo(ROLE);
    }

    @Test
    @DisplayName("토큰이 만료되면 예외가 발생한다.")
    void expired_token_test() {
        String expiredToken = expiredToken();

        assertThatThrownBy(() -> jwtService.validateToken(expiredToken))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    @DisplayName("만료된 토큰으로 payload를 조회할 경우 예외가 발생한다.")
    void get_payload_expired_token_test() {
        String expiredToken = expiredToken();

        assertThatThrownBy(() -> jwtService.getPayloadFormToken(expiredToken))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    @DisplayName("유효하지 않은 토큰 형식으로 payload를 조회할 경우 예외가 발생한다.")
    void get_payload_invalid_token_test() {
        String invalidToken = invalidToken();

        assertThatThrownBy(() -> jwtService.getPayloadFormToken(invalidToken))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    @DisplayName("유효하지 않은 secret key를 가진 토큰으로 payload를 조회할 경우 예외가 발생한다.")
    void get_payload_invalid_secret_key_test() {
        String invalidSecretKeyToken = invalidSecretKeyToken();

        assertThatThrownBy(() -> jwtService.getPayloadFormToken(invalidSecretKeyToken))
                .isInstanceOf(AuthenticationException.class);
    }

    private String expiredToken() {
        Date iat = new Date();
        Date exp = new Date(iat.getTime() - 1);
        return Jwts.builder()
                .setSubject(Long.toString(ID))
                .claim("email", EMAIL)
                .claim("role", ROLE)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private String invalidSecretKeyToken() {
        JwtService invalidSecretKeyToken = new JwtService(INVALID_SECRET_KEY, accessTokenExpireInMilliseconds, refreshTokenExpireInMilliseconds);
        return invalidSecretKeyToken.createAccessToken(ID, EMAIL, ROLE);
    }

    private String invalidToken() {
        return null;
    }

}
