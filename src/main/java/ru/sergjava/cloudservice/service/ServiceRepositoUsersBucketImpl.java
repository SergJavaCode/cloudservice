package ru.sergjava.cloudservice.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.sergjava.cloudservice.model.User;
import ru.sergjava.cloudservice.repository.RepositoryUsersBucketInt;

@Service
public class ServiceRepositoUsersBucketImpl implements ServiceUsersBucketInt {
    private final RepositoryUsersBucketInt repositoryUsersBucketInt;

    public ServiceRepositoUsersBucketImpl(RepositoryUsersBucketInt repositoryUsersBucketInt) {
        this.repositoryUsersBucketInt = repositoryUsersBucketInt;
    }


    @Override
    public User login(String login, String password) {
        User user = repositoryUsersBucketInt.login(login);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return repositoryUsersBucketInt.login(userDetails.getUsername());
    }
}
