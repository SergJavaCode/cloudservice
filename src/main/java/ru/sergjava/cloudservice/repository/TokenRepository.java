package ru.sergjava.cloudservice.repository;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ru.sergjava.cloudservice.model.AuthToken;

import java.util.HashMap;
@Log4j2
@Component
public class TokenRepository {
    private HashMap<String, AuthToken> tokens;

    public TokenRepository() {
        tokens = new HashMap();
    }

    public AuthToken getTokenByName(String name) {
        return tokens.get(name);
    }

    public void saveToken(AuthToken token) {
        tokens.put(token.getUserName(), token);
        log.info("Токен сохранён.");
    }

    public void delTokenByName(String name) {
        tokens.remove(name);
        log.info("Токен удален.");
    }
}
