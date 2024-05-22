package ru.sergjava.cloudservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.sergjava.cloudservice.dto.UserDto;
import ru.sergjava.cloudservice.exceptions.BadRequestExceptionCust;
import ru.sergjava.cloudservice.repository.BucketRepository;
import ru.sergjava.cloudservice.repository.UsersRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//!!! перед выполнением теста необходимо ОТКЛЮЧИТЬ liquibase в application.properties
@SpringBootTest
@AutoConfigureMockMvc
class CloudserviceApplicationIntegrationTests {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsersRepository repository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BucketRepository bucketRepository;
    @Autowired
    HttpServletResponse response;

    @Test
    void contextLoads() {
    }

    //!!! перед выполнением теста необходимо ОТКЛЮЧИТЬ liquibase в application.properties
    @Test
    @DisplayName("POST /login возвращает HTTP-ответ со статусом 200 OK и auth-token")
    public void givenUserWhenCreditionalsIsOk() throws Exception {
        UserDto userDto = new UserDto("user@mail.ru", "password");
        mockMvc.perform(
                        post("/login")
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth-token").isNotEmpty());
    }

    @Test
    public void givenUserWhenCreditionalsIsNotOk() throws Exception {
        UserDto userDto = new UserDto("userr@mail.ru", "password");
        BadRequestExceptionCust thrown = Assertions.assertThrows(BadRequestExceptionCust.class, () -> {
            mockMvc.perform(
                    post("/login")
                            .content(objectMapper.writeValueAsString(userDto))
                            .contentType(MediaType.APPLICATION_JSON)
            );
        });
        Assertions.assertEquals("Bad credentials", thrown.getMessage());
    }

    @Test
    public void getAuthoritieTest() {
        Assertions.assertEquals("ROLE_USER", repository.getUserByName("user@mail.ru").getAuthorities().get(0).getAuthority());
        ;

    }

    @Test
    public void bucketRepositoryTest() {
        assertDoesNotThrow(() -> {
            bucketRepository.listFiles(repository.getUserByName("user@mail.ru"), 3).stream().forEach(System.out::println);
        });
    }
}
