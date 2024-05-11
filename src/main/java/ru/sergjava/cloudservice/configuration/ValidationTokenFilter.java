package ru.sergjava.cloudservice.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sergjava.cloudservice.service.JwtTokenHandler;

import java.io.IOException;

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
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "/login".equals(path);

    }
}
