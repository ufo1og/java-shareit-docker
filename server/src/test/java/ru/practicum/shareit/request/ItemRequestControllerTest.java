package ru.practicum.shareit.request;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;
    @MockBean
    private ItemRequestService itemRequestService;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Хочу пилить - нужна пила",
            LocalDateTime.now().minusDays(1), null);

    @Test
    public void testAdd_ThenOK() throws Exception {
        when(itemRequestService.add(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    public void testAdd_WhenValidationFail_ThenBadRequest() throws Exception {
        when(itemRequestService.add(any(ItemRequestDto.class), anyLong()))
                .thenThrow(new ValidationFailException(""));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllOwnRequests_ThenOK() throws Exception {
        when(itemRequestService.getAllOwnRequests(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class));
    }

    @Test
    public void testGetAllOwnRequests_WhenNotFound_ThenNotFound() throws Exception {
        when(itemRequestService.getAllOwnRequests(anyLong()))
                .thenThrow(new NoSuchElementException());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllRequests_ThenOK() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), any()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class));
    }

    @Test
    public void testGetAllRequests_WhenNotFound_ThenNotFound() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), any()))
                .thenThrow(new NoSuchElementException());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllRequests_WhenValidationFail_ThenBadRequest() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), any()))
                .thenThrow(new ValidationFailException(""));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetRequestById_ThenOK() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class));
    }

    @Test
    public void testGetRequestById_WhenNotFound_ThenNotFound() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenThrow(new NoSuchElementException());

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
