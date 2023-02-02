package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking, User booker, Item item) {
        return new BookingDto(booking.getId(), booking.getStartDate(), booking.getEndDate(), booking.getStatus(),
                booker, item);
    }

    public static Booking toBooking(AddBookingDto bookingDto, User booker) {
        Booking booking = new Booking();
        booking.setStartDate(bookingDto.getStart());
        booking.setEndDate(bookingDto.getEnd());
        booking.setItemId(bookingDto.getItemId());
        booking.setBookerId(booker.getId());
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        return booking != null ? new BookingInfoDto(booking) : null;
    }
}
