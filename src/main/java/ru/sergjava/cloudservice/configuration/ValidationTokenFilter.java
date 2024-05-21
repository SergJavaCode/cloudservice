package ru.sergjava.cloudservice.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sergjava.cloudservice.exceptions.UnauthorizedExceptionCust;
import ru.sergjava.cloudservice.service.JwtTokenHandler;

import java.io.IOException;
@Log4j2
@Component
public class ValidationTokenFilter extends OncePerRequestFilter {
    private JwtTokenHandler jwtTokenHandler;

    public ValidationTokenFilter(JwtTokenHandler jwtTokenHandler) {
        this.jwtTokenHandler = jwtTokenHandler;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //если токен недействителен выбрасывваем ошибку в ответ и  прерываем обработку запроса
        if (!jwtTokenHandler.validationToken(request)) {
            log.error("Unauthorized");
            throw new UnauthorizedExceptionCust("Unauthorized");
        }
        log.info("Токен действителен.");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "/login".equals(path);

    }
}
