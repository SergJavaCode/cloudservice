package ru.sergjava.cloudservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sergjava.cloudservice.configuration.BodyUserDetailsAuthenticationProvider;
import ru.sergjava.cloudservice.dto.ErrorDto;
import ru.sergjava.cloudservice.dto.UserDto;
import ru.sergjava.cloudservice.model.AuthToken;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@Component
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    public InitialAuthenticationFilter(BodyUserDetailsAuthenticationProvider bodyUserDetailsAuthenticationProvider, JwtTokenRepository jwtTokenRepository) {
        this.bodyUserDetailsAuthenticationProvider = bodyUserDetailsAuthenticationProvider;
        this.jwtTokenRepository=jwtTokenRepository;
    }
    private final BodyUserDetailsAuthenticationProvider bodyUserDetailsAuthenticationProvider;
    private JwtTokenRepository jwtTokenRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //аутентификация по JSON из Body
        String bodyJson = request.getReader().lines().collect(Collectors.joining()); //читаем всё тело
        if (bodyJson != null) {
            ObjectMapper mapper = new ObjectMapper();
            UserDto userDto = mapper.readValue(bodyJson, UserDto.class);
            String username = userDto.getLogin();
            String password = userDto.getPassword();
            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
                bodyUserDetailsAuthenticationProvider.authenticate(authentication);

                //работа с токеном
                CsrfToken csrfToken = jwtTokenRepository.loadToken(request);
                AuthToken authToken = null;
                if(csrfToken==null){   //если токен еще не выдавался, генерируе его и сохраняем
                    csrfToken= jwtTokenRepository.generateToken(request);
                    jwtTokenRepository.saveToken(csrfToken,request,response);
                    authToken = new AuthToken(csrfToken.getToken());
                }
//                request.setAttribute(CsrfToken.class.getName(), csrfToken);
//                request.setAttribute(csrfToken.getParameterName(), csrfToken);

                response.setHeader("Authorization", "OK");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
            //  objectMapper.writeValue(response.getOutputStream(), authToken);
            } catch (BadCredentialsException | UsernameNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                //пишем в тело json error
                ObjectMapper objectMapper = new ObjectMapper();
                PrintWriter out = null;
                out=response.getWriter();
                ErrorDto errorDto=new ErrorDto(e.getMessage());
                String jsonErrorDto = objectMapper.writeValueAsString(errorDto);
                out.println(jsonErrorDto);
                out.close();
                logger.error(errorDto);
            }

        }
       // filterChain.doFilter(request,response);
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        return !request.getServletPath().equals("/login");
//    }
}
