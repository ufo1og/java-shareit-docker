package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.ValidationFailException;

import java.util.Optional;

public class Utils {
    public static final int DEFAULT_PAGE_SIZE = 20;

    public static PageRequest getPageRequest(Integer from, Optional<Integer> size) {
        if (from < 0 || (size.isPresent() && size.get() < 0)) {
            throw new ValidationFailException("Parameters 'from' and 'size' must be positive!");
        }
        if (size.isEmpty()) {
            return PageRequest.of(from, DEFAULT_PAGE_SIZE);
        } else {
            return PageRequest.of(from, size.get());
        }
    }
}
