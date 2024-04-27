package ru.sergjava.cloudservice.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.ContextExposingHttpServletRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sergjava.cloudservice.configuration.JwtTokenRepository;
import ru.sergjava.cloudservice.model.AuthToken;
import ru.sergjava.cloudservice.model.User;
import ru.sergjava.cloudservice.service.ServiceRepositoUsersBucketImpl;

@RestController
public class FileOperations {
    private ServiceRepositoUsersBucketImpl serviceRepositoUsersBucketImpl;
    private  JwtTokenRepository jwtTokenRepository;

    public FileOperations(ServiceRepositoUsersBucketImpl serviceRepositoUsersBucketImple, JwtTokenRepository jwtTokenRepository) {
        this.serviceRepositoUsersBucketImpl = serviceRepositoUsersBucketImple;
        this.jwtTokenRepository=jwtTokenRepository;
    }
    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/login") // переделать потом на POST
    public void login(@RequestBody String login, @RequestBody String password) {


    }
    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/str") // переделать потом на POST
    public String str() {

        return "HIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";
    }

}
