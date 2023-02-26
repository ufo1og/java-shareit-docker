package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.practicum.shareit.booking.Booking;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BookingInfoDto {
    @Positive
    private final Long id;
    @Positive
    private final Long bookerId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public BookingInfoDto(Booking booking) {
        this.id = booking.getId();
        this.bookerId = booking.getBookerId();
        this.startDate = booking.getStartDate();
        this.endDate = booking.getEndDate();
    }
}

