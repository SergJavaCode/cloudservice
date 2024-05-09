package ru.sergjava.cloudservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sergjava.cloudservice.dto.ErrorDto;
import ru.sergjava.cloudservice.dto.UserDto;
import ru.sergjava.cloudservice.model.AuthToken;
import ru.sergjava.cloudservice.service.JwtTokenHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Component
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    private final BodyUserDetailsAuthenticationProvider bodyUserDetailsAuthenticationProvider;
    private JwtTokenHandler jwtTokenHandler;

    public InitialAuthenticationFilter(BodyUserDetailsAuthenticationProvider bodyUserDetailsAuthenticationProvider, JwtTokenHandler jwtTokenHandler) {
        this.bodyUserDetailsAuthenticationProvider = bodyUserDetailsAuthenticationProvider;
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // request.setAttribute(HttpServletResponse.class.getName(), response);
        //аутентификация по JSON из Body
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (request.getContentType() == null || request.getContentType().contains("multipart/form-data")) {////??????надо ли
            filterChain.doFilter(request, response);
        }


        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null && request.getHeader("auth-token")==null) {
            String bodyJson = request.getReader().lines().collect(Collectors.joining()); //читаем всё тело
            ObjectMapper mapper = new ObjectMapper();
            UserDto userDto = mapper.readValue(bodyJson, UserDto.class);
            String userName = userDto.getLogin();
            String password = userDto.getPassword();


            try {
                authentication = new UsernamePasswordAuthenticationToken(userName, password);
                authentication = bodyUserDetailsAuthenticationProvider.authenticate(authentication);
                if (authentication == null) {
                    this.logger.trace("Не обработан запрос на аутентификацию, так как не удалось найти имя пользователя и пароль в заголовке базовой авторизации");
                    filterChain.doFilter(request, response);
                    return;
                }
                SecurityContext context = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authentication);
                HttpSession session = request.getSession(true);
                //Store the security context in the session to facilitate other operations of the user in the same session
                session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
                //работа с токеном
                Optional<AuthToken> authToken = jwtTokenHandler.loadToken(userName);
                //AuthToken authToken = null;
                if (authToken.isEmpty()) {   //если токен еще не выдавался, генерируем его и сохраняем
                    authToken = Optional.ofNullable(jwtTokenHandler.generateToken(response, userName)); //генерируем и сохраняем токен
                }
                response.setHeader("Authorization", "OK");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json;charset=UTF-8");
            } catch (BadCredentialsException | UsernameNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");

                //пишем в тело json error
                ObjectMapper objectMapper = new ObjectMapper();
                PrintWriter out = null;
                out = response.getWriter();
                ErrorDto errorDto = new ErrorDto(e.getMessage());
                String jsonErrorDto = objectMapper.writeValueAsString(errorDto);
                out.println(jsonErrorDto);
                out.close();
                logger.error(errorDto);
            }


        }

    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        return request.getServletPath().contains("/file");
//    }
}
