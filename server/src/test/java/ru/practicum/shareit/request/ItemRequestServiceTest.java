package ru.practicum.shareit.request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private ItemRequestService itemRequestService;

    @BeforeEach
    public void setItemRequestService() {
        this.itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    public void testAdd_WhenUserNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> itemRequestService.add(null, 1L)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testAdd_StandardBehaviour_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(new ItemRequest());

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "item pls", LocalDateTime.now(), null);
        itemRequestService.add(itemRequestDto, 1L);

        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(Mockito.any(ItemRequest.class));
    }

    @Test
    public void testGetAllOwnRequests_WhenUserNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> itemRequestService.getAllOwnRequests(1L)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetAllOwnRequests_StandardBehaviour_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRequestRepository.findAllByCreatorIdOrderByCreatedDesc(Mockito.anyLong()))
                .thenReturn(Collections.singletonList(new ItemRequest(1L, "item pls", LocalDateTime.now(), 1L)));
        Mockito.when(itemRepository.findAllByRequestIdIn(Mockito.anyList()))
                .thenReturn(Collections.emptyList());

        itemRequestService.getAllOwnRequests(1L);

        Mockito.verify(itemRequestRepository, Mockito.times(1)).findAllByCreatorIdOrderByCreatedDesc(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByRequestIdIn(Collections.singletonList(1L));
    }

    @Test
    public void testGetAllRequests_WhenSizeIsNull_ThenReturnEmptyCollection() {
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(1L, 0, Optional.empty());

        assertThat(requests, is(equalTo(Collections.emptyList())));
    }

    @Test
    public void testGetAllRequests_WhenSizeIsBelowZero_ThenThrow() {
        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> itemRequestService.getAllRequests(1L, 0, Optional.of(-1))
        );

        assertThat(e.getMessage(), is(equalTo("Parameters 'from' and 'size' must be positive!")));
    }

    @Test
    public void testGetAllRequests_WhenFromIsBelowZero_ThenThrow() {
        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> itemRequestService.getAllRequests(1L, -1, Optional.of(2))
        );

        assertThat(e.getMessage(), is(equalTo("Parameters 'from' and 'size' must be positive!")));
    }

    @Test
    public void testGetAllRequests_WhenUserNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> itemRequestService.getAllRequests(1L, 0, Optional.of(2))
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetAllRequests_StandardBehaviour_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRequestRepository.findAllByCreatorIdNotOrderByCreatedDesc(Mockito.anyLong(),
                Mockito.any(Pageable.class))).thenReturn(Collections.emptyList());

        itemRequestService.getAllRequests(1L, 0, Optional.of(2));

        Mockito.verify(itemRequestRepository, Mockito.times(1)).findAllByCreatorIdNotOrderByCreatedDesc(1L,
                PageRequest.of(0, 2));
    }

    @Test
    public void testGetById_WhenUserNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> itemRequestService.getById(1L, 1L)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetById_WhenItemRequestNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> itemRequestService.getById(1L, 1L)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetById_StandardBehaviour_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new ItemRequest(1L, "item pls", LocalDateTime.now(), 1L)));

        itemRequestService.getById(1L, 1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(1L);
    }
}
