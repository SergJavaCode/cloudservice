package ru.sergjava.cloudservice.repository;

import org.springframework.stereotype.Component;
import ru.sergjava.cloudservice.model.AuthToken;

import java.util.HashMap;

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
    }

    public void delTokenByName(String name) {
        tokens.remove(name);
    }
}
