package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        validateEmail(userDto.getEmail());
        return post("", userDto);
    }

    public ResponseEntity<Object> getUserById(long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> update(long userId, UserDto userDto) {
        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
        }
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> delete(long userId) {
        return delete("/" + userId);
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    private void validateEmail(String email) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.find()) {
            throw new ValidationFailException(String.format("Email '%s' is not valid!", email));
        }
    }
}
