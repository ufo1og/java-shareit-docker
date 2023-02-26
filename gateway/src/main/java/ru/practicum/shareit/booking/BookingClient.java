package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.ValidationFailException;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> consider(long userId, long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBookingById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookerBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "state", state.name(),
                "from", from
        ));
        if (from < 0) {
            throw new ValidationFailException("Parameter 'from' can't be negative!");
        }
        if (size == null) {
            return get("?state={state}&from={from}", userId, parameters);
        }
        if (size < 0) {
            throw new ValidationFailException("Parameter 'size' can't be negative!");
        }
        parameters.put("size", size);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllOwnerBookings(Long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "state", state.name(),
                "from", from
        ));
        if (from < 0) {
            throw new ValidationFailException("Parameter 'from' can't be negative!");
        }
        if (size == null) {
            return get("/owner?state={state}&from={from}", userId, parameters);
        }
        if (size < 0) {
            throw new ValidationFailException("Parameter 'size' can't be negative!");
        }
        parameters.put("size", size);
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
