package ru.practicum.shareit.booking;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;
    @MockBean
    private BookingService bookingService;

    private final BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(5),
            LocalDateTime.now().plusDays(1), BookingStatus.APPROVED, new User(1L, "John", "john@ya.ru"),
            new Item(1L, "дрель", "хорошо сверлит", true, 2L, null));

    @Test
    public void testAdd_ThenOK() throws Exception {
        AddBookingDto addBookingDto = new AddBookingDto(1L, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(1));

        when(bookingService.add(anyLong(), any(AddBookingDto.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(addBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void testAdd_WhenIdIsNull_ThenBadRequest() throws Exception {
        AddBookingDto badDto = new AddBookingDto(null, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(1));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(badDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAdd_WhenIdIsNegative_ThenBadRequest() throws Exception {
        AddBookingDto badDto = new AddBookingDto(-1L, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(1));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(badDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAdd_WhenStartIsNull_ThenBadRequest() throws Exception {
        AddBookingDto badDto = new AddBookingDto(1L, null, LocalDateTime.now().plusDays(1));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(badDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAdd_WhenEndIsNull_ThenBadRequest() throws Exception {
        AddBookingDto badDto = new AddBookingDto(null, LocalDateTime.now().minusDays(5), null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(badDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testConsider_ThenOK() throws Exception {
        when(bookingService.consider(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testConsider_WhenWithoutApprovedParam_ThenBadRequest() throws Exception {
        when(bookingService.consider(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testConsider_WhenSomeNotFound_ThenNotFound() throws Exception {
        when(bookingService.consider(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NoSuchElementException());

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testConsider_WhenValidationFail_ThenBadRequest() throws Exception {
        when(bookingService.consider(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new ValidationFailException(""));

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testConsider_WhenForbiddenAccess_ThenForbidden() throws Exception {
        when(bookingService.consider(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new ForbiddenAccessException(""));

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetById_ThenOK() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void testGetById_WhenNotFound_ThenNotFound() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenThrow(new NoSuchElementException());

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllBookerBookings_ThenOK() throws Exception {
        when(bookingService.getAllBookerBookings(anyLong(), anyString(), anyInt(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void testGetAllBookerBookings_WhenUnsupportedState_ThenInternalServerError() throws Exception {
        when(bookingService.getAllBookerBookings(anyLong(), anyString(), anyInt(), any()))
                .thenThrow(new UnsupportedStateException(""));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetAllBookerBookings_WhenNotFound_ThenNotFound() throws Exception {
        when(bookingService.getAllBookerBookings(anyLong(), anyString(), anyInt(), any()))
                .thenThrow(new NoSuchElementException());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllBookerBookings_WhenValidationFail_ThenBadRequest() throws Exception {
        when(bookingService.getAllBookerBookings(anyLong(), anyString(), anyInt(), any()))
                .thenThrow(new ValidationFailException(""));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllOwnerBookings_ThenOk() throws Exception {
        when(bookingService.getAllOwnerBookings(anyLong(), anyString(), anyInt(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void testGetAllOwnerBookings_WhenUnsupportedState_ThenInternalServerError() throws Exception {
        when(bookingService.getAllOwnerBookings(anyLong(), anyString(), anyInt(), any()))
                .thenThrow(new UnsupportedStateException(""));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetAllOwnerBookings_WhenNotFound_ThenNotFound() throws Exception {
        when(bookingService.getAllOwnerBookings(anyLong(), anyString(), anyInt(), any()))
                .thenThrow(new NoSuchElementException());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllOwnerBookings_WhenValidationFail_ThenBadRequest() throws Exception {
        when(bookingService.getAllOwnerBookings(anyLong(), anyString(), anyInt(), any()))
                .thenThrow(new ValidationFailException(""));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
