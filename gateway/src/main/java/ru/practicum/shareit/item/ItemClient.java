package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(long userId, long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemByUser(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>(Map.of("from", from));
        if (from < 0) {
            throw new ValidationFailException("Parameter 'from' can't be negative!");
        }
        if (size == null) {
            return get("?from={from}", userId, parameters);
        }
        if (size < 0) {
            throw new ValidationFailException("Parameter 'size' can't be negative!");
        }
        parameters.put("size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> searchItems(String text, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "text", text,
                "from", from
        ));
        if (from < 0) {
            throw new ValidationFailException("Parameter 'from' can't be negative!");
        }
        if (size == null) {
            return get("/search?text={text}&from={from}", null, parameters);
        }
        if (size < 0) {
            throw new ValidationFailException("Parameter 'size' can't be negative!");
        }
        parameters.put("size", size);
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, Long itemId, CommentCreateDto commentDto) {
        return put("/" + itemId + "/comment", userId, commentDto);
    }
}
