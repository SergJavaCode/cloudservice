package ru.sergjava.cloudservice.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.ContentType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.server.header.ContentTypeOptionsServerHttpHeadersWriter;
import org.springframework.stereotype.Repository;
import ru.sergjava.cloudservice.model.AuthToken;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Repository
public class JwtTokenRepository implements CsrfTokenRepository {

    @Getter
    private String secret;

    @Value("${app.jwt-token.expiration}")
    private Integer envExpirationJwt;

    public JwtTokenRepository() {
        this.secret = SecurityContextHolder.getContext().getAuthentication().getName() + System.nanoTime();
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest httpServletRequest) {
        String id = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(envExpirationJwt)
                .atZone(ZoneId.systemDefault()).toInstant());

        String token = "";
        try {
            token = Jwts.builder()
                    .setId(id)
                    .setIssuedAt(now)
                    .setNotBefore(now)
                    .setExpiration(exp)
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        } catch (JwtException e) {
            e.printStackTrace();
            //ignore
        }
        return new DefaultCsrfToken("x-csrf-token", "_csrf", token);
    }

    @Override
    public void saveToken(CsrfToken csrfToken, HttpServletRequest request, HttpServletResponse response) {
        String jsonAuthToken;
        PrintWriter out = null;
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("tpplication/json;charset=UTF-8");
        AuthToken authToken = new AuthToken(csrfToken.getToken());

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

    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }
}