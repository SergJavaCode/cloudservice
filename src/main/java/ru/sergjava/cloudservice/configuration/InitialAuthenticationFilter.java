package ru.sergjava.cloudservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sergjava.cloudservice.configuration.BodyUserDetailsAuthenticationProvider;
import ru.sergjava.cloudservice.dto.UserDto;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    public InitialAuthenticationFilter(BodyUserDetailsAuthenticationProvider bodyUserDetailsAuthenticationProvider) {
        this.bodyUserDetailsAuthenticationProvider = bodyUserDetailsAuthenticationProvider;
    }

    private final BodyUserDetailsAuthenticationProvider bodyUserDetailsAuthenticationProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        AbstractUserDetailsAuthenticationProvider abstractUserDetailsAuthenticationProvider;
        String bodyJson = request.getReader().lines().collect(Collectors.joining());
        if (bodyJson != null) {
            ObjectMapper mapper = new ObjectMapper();
            UserDto userDto = mapper.readValue(bodyJson, UserDto.class);
            String username = userDto.getLogin();
            String password = userDto.getPassword();
            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
                authentication = bodyUserDetailsAuthenticationProvider.authenticate(authentication);
                response.setHeader("Authorization", "OK");
            } catch (BadCredentialsException e) {
                logger.error(e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/login");
    }
}
