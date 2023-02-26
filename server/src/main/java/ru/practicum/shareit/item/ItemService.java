package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userID, long itemId, ItemDto itemDto);

    ItemDto getById(long userId, long itemId);

    List<ItemDto> getByUser(long userId, Integer from, Optional<Integer> size);

    List<ItemDto> searchItems(String text, Integer from, Optional<Integer> size);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto text);
}
