package ru.sergjava.cloudservice;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.sergjava.cloudservice.dto.ListDto;
import ru.sergjava.cloudservice.model.AuthToken;
import ru.sergjava.cloudservice.model.User;
import ru.sergjava.cloudservice.repository.BucketRepository;
import ru.sergjava.cloudservice.repository.TokenRepository;
import ru.sergjava.cloudservice.repository.UsersRepository;
import ru.sergjava.cloudservice.service.JwtTokenHandler;
import ru.sergjava.cloudservice.service.ServiceUsersBucketImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
public class CloudserviceApplicationModuleTests {


    @InjectMocks
    ServiceUsersBucketImpl serviceUsersBucket;
    @InjectMocks
    JwtTokenHandler jwtTokenHandler;

    @Mock
    TokenRepository tokenRepository;

    @Mock
    BucketRepository bucketRepository;

    @Mock
    UsersRepository usersRepository;
    @Mock
    SecurityContext securityContext;
    @Mock
    Authentication authentication;

    @Test
    @DisplayName("Возврат NULL если список полученный из репозитория список файлов пуст.")
    void listFilesReturnEmpty() {
        User user = new User();
        user.setUsername("user");
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(bucketRepository.listFiles(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<ListDto>());
        Mockito.when(usersRepository.getUserByName(Mockito.any())).thenReturn(user);
        Assert.assertNull(serviceUsersBucket.listFiles(6));
    }

    @Test
    @DisplayName("Проверяем правильность генерации authToken.")
    void generateTokenIsNotNull() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            Field field = jwtTokenHandler.getClass().getDeclaredField("secret"); //устанавливаем значение приватного поля через рефлексию
            field.setAccessible(true);
            field.set(jwtTokenHandler, "23sdkfjLQETWEQWEVsdfhlieuewto7ahdlidaiwe6wetqywfmaFSDMAVDMAJSFDAJSDVHJHTQW23");
            field = jwtTokenHandler.getClass().getDeclaredField("envExpirationJwt");
            field.setAccessible(true);
            field.set(jwtTokenHandler, 30);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Assert.assertTrue(false);
        }
        AuthToken authToken = jwtTokenHandler.generateToken(response, "user@mail.ru");
        Assert.assertNotNull(authToken);
        Assert.assertNotNull(authToken.getUserName());
        Assert.assertEquals("user@mail.ru", authToken.getUserName());
        Assert.assertNotNull(authToken.getAuthToken());
        System.out.println(authToken);
    }


}
