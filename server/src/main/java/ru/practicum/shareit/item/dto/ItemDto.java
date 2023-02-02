package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;


@Getter
@AllArgsConstructor
public class ItemDto {
    @Positive
    private final Long id;
    @NotBlank(groups = {Create.class})
    private final String name;
    @NotBlank(groups = {Create.class})
    private final String description;
    @NotNull(groups = {Create.class})
    private final Boolean available;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
