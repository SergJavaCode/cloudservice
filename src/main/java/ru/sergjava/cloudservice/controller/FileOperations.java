package ru.sergjava.cloudservice.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sergjava.cloudservice.model.User;
import ru.sergjava.cloudservice.service.ServiceRepositoUsersBucketImpl;

@RestController
public class FileOperations {
    private ServiceRepositoUsersBucketImpl serviceRepositoUsersBucketImpl;


    public FileOperations(ServiceRepositoUsersBucketImpl serviceRepositoUsersBucketImple) {
        this.serviceRepositoUsersBucketImpl = serviceRepositoUsersBucketImple;

    }
    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/login") // переделать потом на POST
    public String login(@RequestBody String login, @RequestBody String password) {
      User user = serviceRepositoUsersBucketImpl.login(login, password);
      return user.getAuthToken();
    }
    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/str") // переделать потом на POST
    public String str() {

        return "HIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";
    }

}
