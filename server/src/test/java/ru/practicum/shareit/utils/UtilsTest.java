package ru.practicum.shareit.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.ValidationFailException;

import java.util.Optional;


public class UtilsTest {
    @Test
    public void testGetPageRequest_WhenFromBelowZero_ThenThrow() {
        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> Utils.getPageRequest(-1, Optional.of(5))
        );

        assertThat(e.getMessage(), is(equalTo("Parameters 'from' and 'size' must be positive!")));
    }

    @Test
    public void testGetPageRequest_WhenFromAboveZero_ThenOK() {
        PageRequest pageRequest = Utils.getPageRequest(1, Optional.of(5));
        PageRequest expectedPageRequest = PageRequest.of(1, 5);

        assertThat(pageRequest, is(equalTo(expectedPageRequest)));
    }

    @Test
    public void testGetPageRequest_WhenSizeIsNull_ThenOK() {
        PageRequest pageRequest = Utils.getPageRequest(1, Optional.empty());
        PageRequest expectedPageRequest = PageRequest.of(1, 20);

        assertThat(pageRequest, is(equalTo(expectedPageRequest)));
    }

    @Test
    public void testGetPageRequest_WhenSizeBelowZero_ThenThrow() {
        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> Utils.getPageRequest(1, Optional.of(-1))
        );

        assertThat(e.getMessage(), is(equalTo("Parameters 'from' and 'size' must be positive!")));
    }
}