package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemShortDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long requestId;
}
