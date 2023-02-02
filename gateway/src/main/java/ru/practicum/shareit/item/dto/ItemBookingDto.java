package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemBookingDto {
    @NotNull
    @Positive
    private Long id;
    @NotNull
    @Positive
    private Long bookerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
