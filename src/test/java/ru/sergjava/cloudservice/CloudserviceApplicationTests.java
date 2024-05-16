package ru.sergjava.cloudservice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.servicelocator.LiquibaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.sergjava.cloudservice.dto.UserDto;
import ru.sergjava.cloudservice.exceptions.BadRequestExceptionCust;
import ru.sergjava.cloudservice.model.AuthToken;
import ru.sergjava.cloudservice.model.User;
import ru.sergjava.cloudservice.repository.UsersRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CloudserviceApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsersRepository repository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    //!!! перед выполнением теста необходимо ОТКЛЮЧИТЬ liquibase в application.properties
    @Test
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
            ) ;
        });
        Assertions.assertEquals("Bad credentials", thrown.getMessage());
    }


}
