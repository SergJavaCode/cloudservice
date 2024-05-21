package ru.sergjava.cloudservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.sergjava.cloudservice.model.AuthToken;
import ru.sergjava.cloudservice.repository.TokenRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
@Log4j2
@Component
public class JwtTokenHandler {

    @Value("${app.jwt-token.secret}")
    private String secret;

    @Value("${app.jwt-token.expiration}")
    private Integer envExpirationJwt;
    public TokenRepository tokenRepository;

    public JwtTokenHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }


    public AuthToken generateToken(HttpServletResponse response, String userName) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonAuthToken;
        PrintWriter out = null;

        String id = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(envExpirationJwt)
                .atZone(ZoneId.systemDefault()).toInstant());
        String token = "";
        token = JWT.create()
                .withIssuer(id)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(Algorithm.HMAC256(secret + System.nanoTime()));
        AuthToken authToken = AuthToken.builder().userName(userName).authToken(token).build();
        tokenRepository.saveToken(authToken);
        response.setContentType("application/json;charset=ISO-8859-1");

        try {
            jsonAuthToken = objectMapper.writeValueAsString(authToken);
        } catch (JsonProcessingException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        try {
            out = response.getWriter();
            out.println(jsonAuthToken);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            assert out != null;
            out.close();
        }
        log.info("Токен успешно сгенерирован.");
        return authToken;
    }

    public boolean validationToken(HttpServletRequest request) {
        String token = request.getHeader("auth-token");
        SecurityContext context = SecurityContextHolder.getContext();
        if (token == null) {
            log.warn("Токен пустой.");
            return false;
        } else {
            token = token.substring(7);
            if (token.equals(tokenRepository.getTokenByName(context.getAuthentication().getPrincipal().toString()).getToken())) {
                log.info("Токен валиден");
                return true;
            } else {
               log.warn("Токен не валиден.");
               return false;
            }
        }
    }

    public void deleteToken(Authentication authentication) {
        if (authentication != null) {
            tokenRepository.delTokenByName(authentication.getPrincipal().toString());
        }

    }

    public Optional<AuthToken> loadToken(String userName) {
        return Optional.ofNullable(tokenRepository.getTokenByName(userName));
    }
}