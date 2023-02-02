package ru.practicum.shareit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShareItTests {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    @Test
    public void testCreateAndThenDeleteUser() {
        UserDto userDto = new UserDto(null, "Абдула", "abdula@ya.ru");
        UserDto expectedUSerDto = new UserDto(11L, userDto.getName(), userDto.getEmail());

        UserDto createdUserDto = userService.create(userDto);

        assertThat(createdUserDto, is(expectedUSerDto));

        UserDto deletedUserDto = userService.delete(expectedUSerDto.getId());

        assertThat(deletedUserDto, is(expectedUSerDto));
    }

    @Test
    public void testItemServiceGetByUser() {
        List<ItemDto> foundItems = itemService.getByUser(1L, 0, Optional.of(10));

        assertThat(foundItems.size(), is(3));

        assertThat(foundItems.get(0).getId(), is(1L));
        assertThat(foundItems.get(0).getLastBooking().getId(), is(5L));
        assertThat(foundItems.get(0).getNextBooking(), is(nullValue()));
        assertThat(foundItems.get(0).getName(), is("Дрель"));
        assertThat(foundItems.get(0).getDescription(), is("Привет соседям"));
        assertThat(foundItems.get(0).getComments().size(), is(1));
        assertThat(foundItems.get(0).getComments().get(0).getId(), is(1L));

        assertThat(foundItems.get(1).getId(), is(2L));
        assertThat(foundItems.get(1).getLastBooking().getId(), is(2L));
        assertThat(foundItems.get(1).getNextBooking(), is(nullValue()));
        assertThat(foundItems.get(1).getName(), is("Отвертка"));
        assertThat(foundItems.get(1).getDescription(), is("Чтобы закрутить"));
        assertThat(foundItems.get(1).getComments().size(), is(1));
        assertThat(foundItems.get(1).getComments().get(0).getId(), is(2L));

        assertThat(foundItems.get(2).getId(), is(3L));
        assertThat(foundItems.get(2).getLastBooking(), is(nullValue()));
        assertThat(foundItems.get(2).getNextBooking().getId(), is(3L));
        assertThat(foundItems.get(2).getName(), is("Гаечный ключ"));
        assertThat(foundItems.get(2).getDescription(), is("На 17-19"));
        assertThat(foundItems.get(2).getComments(), is(nullValue()));
    }

    @Test
    public void testGetAllOwnerBookings() {
        List<BookingDto> foundBookings = bookingService.getAllOwnerBookings(1L, "ALL", 0, Optional.empty());

        assertThat(foundBookings.size(), is(5));
        assertThat(foundBookings.get(0).getId(), is(3L));
        assertThat(foundBookings.get(1).getId(), is(4L));
        assertThat(foundBookings.get(2).getId(), is(5L));
        assertThat(foundBookings.get(3).getId(), is(2L));
        assertThat(foundBookings.get(4).getId(), is(1L));

        foundBookings = bookingService.getAllOwnerBookings(2L, "CURRENT", 0, Optional.empty());

        assertThat(foundBookings.size(), is(1));
        assertThat(foundBookings.get(0).getId(), is(6L));

        foundBookings = bookingService.getAllOwnerBookings(1L, "PAST", 0, Optional.empty());

        assertThat(foundBookings.size(), is(3));
        assertThat(foundBookings.get(0).getId(), is(5L));
        assertThat(foundBookings.get(1).getId(), is(2L));
        assertThat(foundBookings.get(2).getId(), is(1L));

        foundBookings = bookingService.getAllOwnerBookings(1L, "FUTURE", 0, Optional.empty());

        assertThat(foundBookings.size(), is(1));
        assertThat(foundBookings.get(0).getId(), is(3L));

        foundBookings = bookingService.getAllOwnerBookings(1L, "WAITING", 0, Optional.empty());

        assertThat(foundBookings.size(), is(1));
        assertThat(foundBookings.get(0).getId(), is(3L));

        foundBookings = bookingService.getAllOwnerBookings(1L, "REJECTED", 0, Optional.empty());

        assertThat(foundBookings.size(), is(1));
        assertThat(foundBookings.get(0).getId(), is(4L));
    }

    @Test
    public void testGetAllRequests() {
        List<ItemRequestDto> foundRequests = itemRequestService.getAllRequests(1L, 0, Optional.of(10));

        assertThat(foundRequests.size(), is(4));
        assertThat(foundRequests.get(0).getId(), is(5L));
        assertThat(foundRequests.get(1).getId(), is(2L));
        assertThat(foundRequests.get(2).getId(), is(1L));
        assertThat(foundRequests.get(3).getId(), is(4L));
    }
}
