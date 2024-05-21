package ru.sergjava.cloudservice.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
@Log4j2
@Component
public class TokenLogoutHandler implements LogoutHandler {
    public TokenLogoutHandler(JwtTokenHandler jwtTokenHandler) {
        this.jwtTokenHandler = jwtTokenHandler;
    }

    JwtTokenHandler jwtTokenHandler;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        jwtTokenHandler.deleteToken(authentication);
        log.info("Выполняется logot. Токен удален.");
    }
}
