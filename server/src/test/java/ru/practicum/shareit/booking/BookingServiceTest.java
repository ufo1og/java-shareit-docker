package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.BookingFailException;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private BookingService bookingService;

    @BeforeEach
    public void setBookingService() {
        this.bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    public void testAdd_WhenUserNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.add(1L, null)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testAdd_WhenItemNotFound_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.add(1L, new AddBookingDto(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2)))
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testAdd_WhenItemNotAvailable_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", false, 2L, null)));

        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> bookingService.add(1L, new AddBookingDto(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2)))
        );

        assertThat(e.getMessage(), is(equalTo("Booking item is not available!")));
    }

    @Test
    public void testAdd_WhenStartDateInPast_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 2L, null)));

        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> bookingService.add(1L, new AddBookingDto(1L, LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(2)))
        );

        assertThat(e.getMessage(), is(equalTo("Booking start date cant be in the past!")));
    }

    @Test
    public void testAdd_WhenEndDateInPast_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 2L, null)));

        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> bookingService.add(1L, new AddBookingDto(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().minusDays(2)))
        );

        assertThat(e.getMessage(), is(equalTo("Booking end date cant be in the past!")));
    }

    @Test
    public void testAdd_WhenEndDateBeforeStartDate_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 2L, null)));

        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> bookingService.add(1L, new AddBookingDto(1L, LocalDateTime.now().plusDays(3),
                        LocalDateTime.now().plusDays(1)))
        );

        assertThat(e.getMessage(), is(equalTo("Booking end date cant be before start date!")));
    }

    @Test
    public void testAdd_WhenBookingYourOwnItem_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 1L, null)));

        BookingFailException e = Assertions.assertThrows(
                BookingFailException.class,
                () -> bookingService.add(1L, new AddBookingDto(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2)))
        );

        assertThat(e.getMessage(), is(equalTo("You cant booking your own items!")));
    }

    @Test
    public void testAdd_StandardBehaviour_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 2L, null)));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(new Booking());

        AddBookingDto addBookingDto = new AddBookingDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.add(1L, addBookingDto);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(new Booking(null, addBookingDto.getStart(),
                addBookingDto.getEnd(), 1L, 1L, BookingStatus.WAITING));
    }

    @Test
    public void testConsider_WhenBookingNOtExists_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.consider(1L, 1L, true)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testConsider_WhenItemNotFound_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.WAITING)));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.consider(1L, 1L, true)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testConsider_WhenBookerTryToChangeStatus_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.WAITING)));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 1L, null)));

        BookingFailException e = Assertions.assertThrows(
                BookingFailException.class,
                () -> bookingService.consider(1L, 1L, true)
        );

        assertThat(e.getMessage(), is(equalTo("Booker can't change booking status!")));
    }

    @Test
    public void testConsider_NotOwnerTryToChangeStatus_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.WAITING)));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 1L, null)));

        ForbiddenAccessException e = Assertions.assertThrows(
                ForbiddenAccessException.class,
                () -> bookingService.consider(2L, 1L, true)
        );

        assertThat(e.getMessage(), is(equalTo("User is not the owner of the booking item!")));
    }

    @Test
    public void testConsider_OwnerNotExists_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.WAITING)));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 2L, null)));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.consider(2L, 1L, true)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testConsider_BookingAlreadyApproved_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.APPROVED)));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 2L, null)));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(2L, "John", "john@ya.ru")));

        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> bookingService.consider(2L, 1L, true)
        );

        assertThat(e.getMessage(), is(equalTo("Booking is already approved!")));
    }

    @Test
    public void testConsider_StandardBehaviour_ThenOK() {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 2L, null)));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(2L, "John", "john@ya.ru")));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(new Booking());

        bookingService.consider(2L, 1L, true);
        booking.setStatus(BookingStatus.APPROVED);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(booking);
    }

    @Test
    public void testGetById_WhenBookingNotExists_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.getById(1L, 1L)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetById_WhenItemNotFound_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.APPROVED)));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.getById(1L, 1L)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetById_WhenUserIsNotTheOwnerOrBooker_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.APPROVED)));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 2L, null)));

        BookingFailException e = Assertions.assertThrows(
                BookingFailException.class,
                () -> bookingService.getById(10L, 11L)
        );

        assertThat(e.getMessage(), is(equalTo("User is not the owner or booker in requested Booking!")));
    }

    @Test
    public void testGetById_WhenUserNotExists_ThenThrow() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.APPROVED)));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 2L, null)));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.getById(1L, 1L)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetById_StandardBehaviour_ThenOK() {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 1L, 1L, BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Item item = new Item(1L, "item", "good item", true, 2L, null);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        User booker = new User(1L, "John", "john@ya.ru");
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booker));

        BookingDto bookingDto = bookingService.getById(1L, 1L);

        assertThat(bookingDto.getId(), is(equalTo(1L)));
        assertThat(bookingDto.getStart(), is(equalTo(booking.getStartDate())));
        assertThat(bookingDto.getStatus(), is(equalTo(booking.getStatus())));
        assertThat(bookingDto.getBooker(), is(equalTo(booker)));
        assertThat(bookingDto.getItem(), is(equalTo(item)));
    }

    @Test
    public void testGetAllBookerBookings_WhenUserNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.getAllBookerBookings(1L, "ALL", 0, Optional.of(10))
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetAllBookerBookings_WhenBadState_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));

        UnsupportedStateException e = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getAllBookerBookings(1L, "TROLOLO", 0, Optional.of(10))
        );

        assertThat(e.getMessage(), is(equalTo("Unknown state: UNSUPPORTED_STATUS")));
    }

    @Test
    public void testGetAllBookerBookings_WhenStateAll_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDateDesc(Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getAllBookerBookings(1L, "ALL", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByBookerIdOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any(Pageable.class));
    }

    @Test
    public void testGetAllBookerBookings_WhenStateCurrent_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getAllBookerBookings(1L, "CURRENT", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    public void testGetAllBookerBookings_WhenStatePast_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getAllBookerBookings(1L, "PAST", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
    }

    @Test
    public void testGetAllBookerBookings_WhenStateFuture_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(bookingRepository.findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getAllBookerBookings(1L, "FUTURE", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
    }

    @Test
    public void testGetAllBookerBookings_WhenStateWaitingOrRejected_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(Mockito.anyLong(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getAllBookerBookings(1L, "WAITING", 0, Optional.of(10));
        bookingService.getAllBookerBookings(1L, "REJECTED", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByBookerIdAndStatusOrderByStartDateDesc(1L,
                BookingStatus.WAITING, PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByBookerIdAndStatusOrderByStartDateDesc(1L,
                BookingStatus.REJECTED, PageRequest.of(0, 10));
    }

    @Test
    public void testGetAllOwnerBookings_WhenUserNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> bookingService.getAllOwnerBookings(1L, "ALL", 0, Optional.of(10))
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetAllOwnerBookings_WhenNoItemsFound_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong()))
                .thenReturn(Collections.emptyList());

        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> bookingService.getAllOwnerBookings(1L, "ALL", 0, Optional.of(10))
        );

        assertThat(e.getMessage(), is(equalTo("No items found for this owner!")));
    }

    @Test
    public void testGetAllOwnerBookings_WhenBadState_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong()))
                .thenReturn(Collections.singletonList(new Item(1L, "item", "good item", true, 1L, null)));

        UnsupportedStateException e = Assertions.assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.getAllOwnerBookings(1L, "TROLOLO", 0, Optional.of(10))
        );

        assertThat(e.getMessage(), is(equalTo("Unknown state: UNSUPPORTED_STATUS")));
    }

    @Test
    public void testGetAllOwnerBookings_WhenStateAll_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong()))
                .thenReturn(Collections.singletonList(new Item(1L, "item", "good item", true, 1L, null)));
        Mockito.when(bookingRepository.findAllByItemIdInOrderByStartDateDesc(Mockito.anyList(),
                Mockito.any(Pageable.class))).thenReturn(Collections.emptyList());

        bookingService.getAllOwnerBookings(1L, "ALL", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByItemIdInOrderByStartDateDesc(
                Collections.singletonList(1L), PageRequest.of(0, 10));
    }

    @Test
    public void testGetAllOwnerBookings_WhenStateCurrent_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong()))
                .thenReturn(Collections.singletonList(new Item(1L, "item", "good item", true, 1L, null)));
        Mockito.when(bookingRepository.findAllByItemIdInAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(Collections.emptyList());

        bookingService.getAllOwnerBookings(1L, "CURRENT", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemIdInAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Mockito.anyList(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    public void testGetAllOwnerBookings_WhenStatePast_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong()))
                .thenReturn(Collections.singletonList(new Item(1L, "item", "good item", true, 1L, null)));
        Mockito.when(bookingRepository.findAllByItemIdInAndEndDateBeforeAndStatusNotOrderByStartDateDesc(
                Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(BookingStatus.class),
                Mockito.any(Pageable.class))).thenReturn(Collections.emptyList());

        bookingService.getAllOwnerBookings(1L, "PAST", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemIdInAndEndDateBeforeAndStatusNotOrderByStartDateDesc(Mockito.anyList(),
                        Mockito.any(LocalDateTime.class), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    public void testGetAllOwnerBookings_WhenStateFuture_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong()))
                .thenReturn(Collections.singletonList(new Item(1L, "item", "good item", true, 1L, null)));
        Mockito.when(bookingRepository.findAllByItemIdInAndStartDateAfterOrderByStartDateDesc(
                Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getAllOwnerBookings(1L, "FUTURE", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemIdInAndStartDateAfterOrderByStartDateDesc(Mockito.anyList(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
    }

    @Test
    public void testGetAllOwnerBookings_WhenStateWaitingOrRejected_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong()))
                .thenReturn(Collections.singletonList(new Item(1L, "item", "good item", true, 1L, null)));
        Mockito.when(bookingRepository.findAllByItemIdInAndStatusOrderByStartDateDesc(Mockito.anyList(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        bookingService.getAllOwnerBookings(1L, "WAITING", 0, Optional.of(10));
        bookingService.getAllOwnerBookings(1L, "REJECTED", 0, Optional.of(10));

        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByItemIdInAndStatusOrderByStartDateDesc(
                Collections.singletonList(1L), BookingStatus.WAITING, PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllByItemIdInAndStatusOrderByStartDateDesc(
                Collections.singletonList(1L), BookingStatus.REJECTED, PageRequest.of(0, 10));
    }
}
