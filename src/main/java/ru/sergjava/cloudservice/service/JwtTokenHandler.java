package ru.sergjava.cloudservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.sergjava.cloudservice.model.AuthToken;
import ru.sergjava.cloudservice.repository.TokenRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

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
            throw new RuntimeException(e);
        }
        try {
            out = response.getWriter();
            out.println(jsonAuthToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert out != null;
            out.close();
        }
        return authToken;
    }

    public boolean validationToken(HttpServletRequest request) {
        String token = request.getHeader("auth-token");
        SecurityContext context = SecurityContextHolder.getContext();
        if (token == null) {
            return false;
        } else {
            token = token.substring(7);
            return token.equals(tokenRepository.getTokenByName(context.getAuthentication().getPrincipal().toString()).getToken());
        }
    }

    public void deleteToken( Authentication authentication) {
        if(authentication!=null){
            tokenRepository.delTokenByName(authentication.getPrincipal().toString());
        }

    }

    public Optional<AuthToken> loadToken(String userName) {
        return Optional.ofNullable(tokenRepository.getTokenByName(userName));
    }
}